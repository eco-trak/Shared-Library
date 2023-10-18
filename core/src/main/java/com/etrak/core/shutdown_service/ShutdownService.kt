package com.etrak.core.shutdown_service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import androidx.lifecycle.LifecycleService
import com.etrak.core.R
import kotlinx.coroutines.launch

class ShutdownService : LifecycleService() {

    /**********************************************************************************************
     * Constants
     *********************************************************************************************/
    companion object {
        const val DEFAULT_DURATION = 10
        const val SHUTDOWN_SEQUENCE_STARTED = "com.ecotrak.shared_library.SHUTDOWN_SEQUENCE_STARTED"
        const val SHUTDOWN_SEQUENCE_COUNTDOWN = "com.ecotrak.shared_library.SHUTDOWN_SEQUENCE_COUNTDOWN"
        const val SHUTDOWN_SEQUENCE_CANCELED = "com.ecotrak.shared_library.SHUTDOWN_SEQUENCE_CANCELED"
        const val EXTRA_DURATION = "com.ecotrak.shared_library.EXTRA_DURATION"
        const val EXTRA_COUNTDOWN = "com.ecotrak.shared_library.EXTRA_COUNTDOWN"

        const val CHANNEL_ID = "shutdown_service"
        const val NOTIFICATION_ID = 1
    }

    enum class Action {
        Start,
        CancelShutdownSequence,
        Stop
    }

    /**********************************************************************************************
     * Variables
     *********************************************************************************************/
    private lateinit var timer: Timer
    private lateinit var gpio: Gpio

    /**********************************************************************************************
     * Helpers
     *********************************************************************************************/
    private fun getContentText(state: Timer.State, countdown: Int? = null) =
        if (state == Timer.State.Started && countdown != null)
            String.format(getString(R.string.shutting_down_system), countdown)
        else
            getString(R.string.monitoring_accessory_power)

    // Build a notification according to a connection status
    private fun createNotification(state: Timer.State, countdown: Int? = null) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.power)
            .setContentTitle(getString(R.string.content_title))
            .setContentText(getContentText(state, countdown))
            .build()

    private fun updateNotification(state: Timer.State, countdown: Int? = null) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, createNotification(state, countdown))
    }

    /**********************************************************************************************
     * Command handlers
     *********************************************************************************************/
    private fun onStart(duration: Int) {

        // Init. the timer
        timer = Timer(duration)

        // Init. the accessory power gpio
        gpio = Gpio(number = "24")
        gpio.mode = Gpio.Mode.Input
        gpio.registerCallback(
            object : Gpio.GpioCallback {
                override fun onValueChanged(value: Gpio.State) {

                    // If the power is off then start the timer
                    if (value == Gpio.State.High) timer.start()

                    // If the power goes on then stop the timer
                    else if(value == Gpio.State.Low) timer.stop()
                }
            }
        )

        // Collect the state of the timer
        lifecycleScope.launch {
            timer.state.collect { state ->
                when (state) {

                    // If the time is up then shutdown the system
                    Timer.State.TimeUp -> {
                        val runtimeObject = Runtime.getRuntime().exec(arrayOf("su", "-c","svc power shutdown"))
                        runtimeObject.waitFor()
                    }

                    // If the timer has started then broadcast SHUTDOWN_SEQUENCE_STARTED
                    Timer.State.Started -> {
                        sendBroadcast(Intent(SHUTDOWN_SEQUENCE_STARTED))
                        updateNotification(timer.state.value)
                    }

                    // If the timer has stopped then broadcast SHUTDOWN_SEQUENCE_CANCELED
                    Timer.State.Stopped -> {
                        sendBroadcast(Intent(SHUTDOWN_SEQUENCE_CANCELED))
                        updateNotification(timer.state.value)
                    }
                }
            }
        }

        // Collect the countdown of the timer
        lifecycleScope.launch {
            timer.countdown.collect { countdown ->

                // Broadcast the countdown
                sendBroadcast(Intent(SHUTDOWN_SEQUENCE_COUNTDOWN).putExtra(EXTRA_COUNTDOWN, countdown))
                updateNotification(timer.state.value, countdown)
            }
        }

        // Create the notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "E-Track shutdown service",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        // Init. the service
        startForeground(1, createNotification(timer.state.value))
    }

    private fun onCancelShutdownSequence() {

        // Stop the timer if the user has clicked the cancel button
        timer.stop()
    }

    private fun onStop() {
        timer.stop()
        gpio.unregisterCallback()
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    /**********************************************************************************************
     * Service onStartCommand function
     *********************************************************************************************/
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            Action.Start.name -> onStart(intent.getIntExtra(EXTRA_DURATION, DEFAULT_DURATION))
            Action.CancelShutdownSequence.name -> onCancelShutdownSequence()
            Action.Stop.name -> onStop()
        }
        return super.onStartCommand(intent, flags, startId)
    }
}
