package com.etrak.core.shutdown_service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.etrak.core.shutdown_service.ShutdownService.Companion.DEFAULT_DURATION
import com.etrak.core.shutdown_service.ShutdownService.Companion.EXTRA_COUNTDOWN
import com.etrak.core.shutdown_service.ShutdownService.Companion.EXTRA_DURATION
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

private const val TAG = "e-trak shutdown-manager"

class ShutdownManager(private val context: Context) {

    @OptIn(ExperimentalCoroutinesApi::class)
    val showCountdownSequence by lazy {
        callbackFlow {
            val receiver = object: BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    Log.d(TAG, "ShutdownManager: onReceive(${intent.action})")

                    when (intent.action) {
                        ShutdownService.SHUTDOWN_SEQUENCE_STARTED -> trySend(true)
                        ShutdownService.SHUTDOWN_SEQUENCE_CANCELED -> trySend(false)
                    }
                }
            }
            context.registerReceiver(
                receiver,
                IntentFilter().apply {
                    addAction(ShutdownService.SHUTDOWN_SEQUENCE_STARTED)
                    addAction(ShutdownService.SHUTDOWN_SEQUENCE_CANCELED)
                }
            )
            awaitClose {
                context.unregisterReceiver(receiver)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val countdown by lazy {
        callbackFlow {
            val receiver = object: BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    trySend(intent.getIntExtra(EXTRA_COUNTDOWN, DEFAULT_DURATION))
                }
            }
            context.registerReceiver(
                receiver,
                IntentFilter().apply {
                    addAction(ShutdownService.SHUTDOWN_SEQUENCE_COUNTDOWN)
                }
            )
            awaitClose {
                context.unregisterReceiver(receiver)
            }
        }
    }

    fun start(duration: Int) {
        Intent(context, ShutdownService::class.java).apply {
            action = ShutdownService.Action.Start.name
            putExtra(EXTRA_DURATION, duration)
            context.startService(this)
        }
    }

    fun cancelShutdownSequence() {
        Intent(context, ShutdownService::class.java).apply {
            action = ShutdownService.Action.CancelShutdownSequence.name
            context.startService(this)
        }
    }
}
