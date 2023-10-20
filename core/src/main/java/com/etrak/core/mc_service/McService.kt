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
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.etrak.core.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.security.InvalidParameterException

private const val TAG = "e-trak mc-service"

abstract class McService(

    private val emulator: Device

) : LifecycleService() {

    companion object {

        // Intents
        const val ON_MESSAGE = "com.etrak.core.mc_service.McService.ON_MESSAGE"
        const val EXTRA_MESSAGE_CODE = "com.etrak.core.mc_service.McService.EXTRA_MESSAGE_CODE"
        const val EXTRA_MESSAGE_PARAMS = "com.etrak.core.mc_service.McService.EXTRA_MESSAGE_PARAMS"

        const val ACTION_CONNECTION_FAILED = "com.etrak.core.mc_service.McService.ACTION_CONNECTION_FAILED"
        const val ACTION_CONNECTION_SUCCEEDED = "com.etrak.core.mc_service.McService.ACTION_CONNECTION_SUCCEEDED"
        const val ACTION_EMULATOR_MODE_ENABLED = "com.etrak.core.mc_service.McService.ACTION_EMULATOR_MODE_ENABLED"
        const val ACTION_DEVICE_UNPLUGGED = "com.etrak.core.mc_service.McService.ACTION_MC_UNPLUGGED"

        const val EXTRA_SET_MODE_MODE = "com.etrak.core.mc_service.McService.EXTRA_SET_MODE_MODE"

        const val CHANNEL_ID = "mc_service"
        const val NOTIFICATION_ID = 1
    }

    // Intent actions
    enum class Action {
        Start,
        SetMode,
        Send,
        Stop
    }

    enum class Mode(val id: Int, @StringRes val resId: Int) {

        StandBy(id = 1, resId = R.string.mode_standby),
        Emulator(id = 2, resId = R.string.mode_emulator),
        Normal(id = 3, resId = R.string.mode_normal);

        fun toInt() = this.id
    }

    private fun Int.toMode() = when (this) {
        1 -> Mode.StandBy
        2 -> Mode.Emulator
        3 -> Mode.Normal
        else -> throw InvalidParameterException()
    }

    private val mode = MutableStateFlow(Mode.StandBy)

    private val standby by lazy { StandByMode() }
    private val normal: Device by lazy { HardwareDevice(this) }

    private lateinit var device: Device

    // When the connection status changes then switch between flows
    @OptIn(ExperimentalCoroutinesApi::class)
    val messages = mode.flatMapLatest { mode ->
        device = when (mode) {
            Mode.Normal -> normal
            Mode.StandBy -> standby
            Mode.Emulator -> emulator
        }
        device.messages
    }
    .shareIn(lifecycleScope, SharingStarted.Eagerly)

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    if (mode.value == Mode.StandBy)
                        setMode(Mode.Normal)
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    if (mode.value == Mode.Normal) {
                        setMode(Mode.StandBy)
                        sendBroadcast(Intent(ACTION_DEVICE_UNPLUGGED))
                    }
                }
            }
        }
    }

    // Build a notification according to a connection status
    private fun createNotification(mode: Mode) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.usb)
            .setContentTitle(getString(R.string.content_title))
            .setContentText(getString(mode.resId))
            .build()

    private fun setMode(mode: Mode) {
        Log.d(TAG, "McService: setMode")

        when (mode) {
            Mode.Normal -> {
                try {
                    normal.connect()
                    this.mode.value = mode
                    sendBroadcast(Intent(ACTION_CONNECTION_SUCCEEDED))
                }
                catch (e: Exception) {
                    sendBroadcast(Intent(ACTION_CONNECTION_FAILED))
                }
            }
            Mode.Emulator -> {
                sendBroadcast(Intent(ACTION_EMULATOR_MODE_ENABLED))
                this.mode.value = mode
            }
            else -> this.mode.value = mode
        }
    }

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
            mode.collect { connectionStatus ->
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
        setMode(Mode.Normal)

        // Init. the service
        startForeground(1, createNotification(mode.value))
    }

    private fun onSetMode(mode: Mode) {
        Log.d(TAG, "McService: onSetMode")
        setMode(mode)
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
            Action.SetMode.name -> onSetMode(
                intent.getIntExtra(
                    EXTRA_SET_MODE_MODE,
                    Mode.StandBy.toInt()
                ).toMode()
            )
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