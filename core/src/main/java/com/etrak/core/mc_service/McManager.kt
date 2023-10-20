package com.etrak.core.mc_service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
    private val _showFailure = MutableStateFlow(false)
    val showDebugDialog = _showFailure.asStateFlow()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_CONNECTION_FAILED)
                _showFailure.value = true
            else if (intent?.action == ACTION_CONNECTION_SUCCEEDED)
                _showFailure.value = false
            else if (intent?.action == ACTION_EMULATOR_MODE_ENABLED)
                _showFailure.value = false
        }
    }

    fun send(msg: Device.Message) {

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

        Log.d(TAG, "McManager: messages")

        // Receiver
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                // Send message
                Log.d(TAG, "McManager: message: onReceive")
                trySend(
                    Device.Message(
                        code = intent.getStringExtra(EXTRA_MESSAGE_CODE)!!,
                        params = intent.getStringArrayExtra(EXTRA_MESSAGE_PARAMS)!!.toList()
                    )
                )
            }
        }

        // Register the receiver
        IntentFilter().apply {
            addAction(ON_MESSAGE)
            context.registerReceiver(receiver, this)
        }

        awaitClose {

            // Unregister the receiver
            Log.d(TAG, "McManager: message: awaitClose")
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
            }
        )

        // Start the service
        Intent(context, service).apply {
            action = McService.Action.Start.name
            context.startService(this)
        }
    }
}
















