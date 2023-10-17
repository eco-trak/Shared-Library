package com.etrak.core.mc_service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.etrak.core.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class McService(

    private val emulator: Device

) : LifecycleService() {

    companion object {

        // Intents
        const val ON_MESSAGE = "com.ecotrak.shared_library.ON_MESSAGE"
        const val EXTRA_MESSAGE_CODE = "com.ecotrak.shared_library.EXTRA_MESSAGE_CODE"
        const val EXTRA_MESSAGE_PARAMS = "com.ecotrak.shared_library.EXTRA_MESSAGE_PARAMS"

        const val CHANNEL_ID = "mc_service"
        const val NOTIFICATION_ID = 1
    }

    // Intent actions
    enum class Action {
        Start,
        Send,
        Stop
    }

    enum class ConnectionStatus(@StringRes val resId: Int) {
        Attached(resId = R.string.attached),
        Detached(resId = R.string.detached),
        Connected(resId = R.string.connected)
    }

    class NoUsbDriverAvailableException : Exception()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {

                // Connect to mc when a device is attached
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    connectionStatus.value = ConnectionStatus.Attached
                    try {
                        hardware.connect()
                    }
                    catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // Disconnect the device when a device is detached
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    hardware.disconnect()
                    connectionStatus.value = ConnectionStatus.Detached
                }
            }
        }
    }
    private val connectionStatus = MutableStateFlow(ConnectionStatus.Detached)
    private val hardware: Device by lazy { HardwareDevice(this) }
    private lateinit var device: Device

    // When the connection status changes then switch between flows
    @OptIn(ExperimentalCoroutinesApi::class)
    val messages = connectionStatus.flatMapLatest { connectionStatus ->
        device = when (connectionStatus) {
            ConnectionStatus.Connected -> hardware
            ConnectionStatus.Detached, ConnectionStatus.Attached -> emulator
        }
        device.messages
    }
    .shareIn(lifecycleScope, SharingStarted.Eagerly)

    // Build a notification according to a connection status
    private fun createNotification(connectionStatus: ConnectionStatus) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.usb)
            .setContentTitle(getString(R.string.content_title))
            .setContentText(getString(connectionStatus.resId))
            .build()

    private fun onStart() {

        // Create the notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        // When the hardware device succeeded in opening to the USB port then update the connection status
        lifecycleScope.launch {
            (hardware as HardwareDevice).connected.collect { connected ->
                if (connected) connectionStatus.value = ConnectionStatus.Connected
            }
        }

        // Broadcast messages emitted from the device
        lifecycleScope.launch {
            messages.collect { msg ->
                sendBroadcast(
                    Intent(ON_MESSAGE).apply {
                        putExtra(EXTRA_MESSAGE_CODE, msg.code)
                        putExtra(EXTRA_MESSAGE_PARAMS, msg.params.toTypedArray())
                    }
                )
            }
        }

        // Update the notification whenever the connection status changes
        lifecycleScope.launch {
            connectionStatus.collect { connectionStatus ->
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(NOTIFICATION_ID, createNotification(connectionStatus))
            }
        }

        // Register the broadcast receiver used to detect whenever an USB device is attached or detached
        IntentFilter().apply {
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
            registerReceiver(receiver, this)
        }

        // Connect to the mc
        try {
            hardware.connect()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        // Init. the service
        startForeground(1, createNotification(connectionStatus.value))
    }

    private fun onSend(msg: Device.Message) {
        device.send(msg)
    }

    private fun onStop() {
        unregisterReceiver(receiver)
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // Dispatch the action to its handler
        when (intent?.action) {
            Action.Start.name -> onStart()
            Action.Send.name -> onSend(
                Device.Message(
                    intent.getStringExtra(EXTRA_MESSAGE_CODE)!!,
                    intent.getStringArrayExtra(EXTRA_MESSAGE_PARAMS)!!.toList()
                )
            )
            Action.Stop.name -> onStop()
        }
        return super.onStartCommand(intent, flags, startId)
    }
}