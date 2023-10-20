package com.etrak.core.mc_service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.util.Log
import com.etrak.core.mc_service.McService.Companion.ACTION_CONNECTION_FAILED
import com.etrak.core.mc_service.McService.Companion.ACTION_CONNECTION_SUCCEEDED
import com.etrak.core.mc_service.McService.Companion.ACTION_EMULATOR_MODE_ENABLED
import com.etrak.core.mc_service.McService.Companion.EXTRA_MESSAGE_CODE
import com.etrak.core.mc_service.McService.Companion.EXTRA_MESSAGE_PARAMS
import com.etrak.core.mc_service.McService.Companion.EXTRA_SET_MODE_MODE
import com.etrak.core.mc_service.McService.Companion.ON_MESSAGE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

private const val TAG = "e-trak mc-manager"

class McManager(

    private val context: Context,
    private val service: Class<*>?

) {
    sealed class Notification {
        object OnConnectionSucceeded: Notification()
        object OnMcAttached: Notification()
    }

    private val _showDebugDialog = MutableStateFlow(false)
    val showDebugDialog = _showDebugDialog.asStateFlow()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "McManager: showDebugDialog receiver(${intent?.action})")

            when (intent?.action) {
                ACTION_CONNECTION_FAILED -> _showDebugDialog.value = true
                ACTION_CONNECTION_SUCCEEDED -> _showDebugDialog.value = false
                ACTION_EMULATOR_MODE_ENABLED -> _showDebugDialog.value = false
                UsbManager.ACTION_USB_DEVICE_DETACHED -> _showDebugDialog.value = true
            }
        }
    }

    fun send(msg: Device.Message) {
        Log.d(TAG, "McManager: send($msg)")

        // Send the message to the service
        Intent(context, service).apply {
            action = McService.Action.Send.name
            putExtra(EXTRA_MESSAGE_CODE, msg.code)
            putExtra(EXTRA_MESSAGE_PARAMS, msg.params.toTypedArray())
            context.startService(this)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val messages = callbackFlow {
        Log.d(TAG, "McManager: messages = callbackFlow {")

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val msg = Device.Message(
                    code = intent.getStringExtra(EXTRA_MESSAGE_CODE)!!,
                    params = intent.getStringArrayExtra(EXTRA_MESSAGE_PARAMS)!!.toList()
                )
//                Log.d(TAG, "McManager: message: onReceive (msg=$msg)")

                trySend(msg)
            }
        }

        IntentFilter().apply {
            addAction(ON_MESSAGE)
            context.registerReceiver(receiver, this)
        }

        awaitClose {
            Log.d(TAG, "McManager: message: awaitClose")
            context.unregisterReceiver(receiver)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val notifications = callbackFlow {
        Log.d(TAG, "McManager: notifications = callbackFlow {")

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                Log.d(TAG, "McManager: notifications receiver(${intent.action})")
                when (intent.action) {
                    ACTION_CONNECTION_SUCCEEDED -> trySend(Notification.OnConnectionSucceeded)
                    UsbManager.ACTION_USB_DEVICE_ATTACHED -> trySend(Notification.OnMcAttached)
                }
            }
        }

        IntentFilter().apply {
            addAction(ACTION_CONNECTION_SUCCEEDED)
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            context.registerReceiver(receiver, this)
        }

        awaitClose {
            Log.d(TAG, "McManager: notifications: awaitClose")
            context.unregisterReceiver(receiver)
        }
    }

    fun setMode(mode: McService.Mode) {
        Log.d(TAG, "McManager: setMode")

        Intent(context, service).apply {
            action = McService.Action.SetMode.name
            putExtra(EXTRA_SET_MODE_MODE, mode.toInt())
            context.startService(this)
        }
    }

    fun start() {
        Log.d(TAG, "McManager: start")

        context.registerReceiver(
            receiver,
            IntentFilter().apply {
                addAction(ACTION_CONNECTION_FAILED)
                addAction(ACTION_CONNECTION_SUCCEEDED)
                addAction(ACTION_EMULATOR_MODE_ENABLED)
//                addAction(ACTION_DEVICE_UNPLUGGED)
                addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
            }
        )

        // Start the service
        Intent(context, service).apply {
            action = McService.Action.Start.name
            context.startService(this)
        }
    }
}
















