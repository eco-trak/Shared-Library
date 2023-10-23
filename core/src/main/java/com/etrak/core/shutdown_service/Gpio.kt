package com.etrak.core.shutdown_service

import android.util.Log
import java.io.DataOutputStream
import java.io.File
import java.util.*

private const val TAG = "e-trak gpio"

class Gpio(private val number: String) {

    enum class State { Low, High }
    enum class Mode { Input, Output }

    interface GpioCallback {
        fun onValueChanged(value: State)
    }

    private val timer = Timer()
    private var callback: GpioCallback? = null
    private var _value: State? = null

    private fun runAsRoot(cmd: String) {
        val process = Runtime.getRuntime().exec("su")
        val dos = DataOutputStream(process.outputStream)
        dos.write(cmd.toByteArray())
        dos.writeBytes("exit\n")
        dos.flush()
        dos.close()
        process.waitFor()
    }

    val value: State
        get() {
            val tmp = if (File("/sys/class/gpio/gpio$number/value").readText().trim() == "1")
                State.High
            else
                State.Low
            Log.d(TAG, "Gpio: val value=$tmp")
            return tmp
        }

    var mode: Mode
        get() {
            return if (File("/sys/class/gpio/gpio$number/direction").readText().trim() == "in")
                Mode.Input
            else
                Mode.Output
        }
        set(value) {
            val cmd = "echo ${ if (value == Mode.Output) "out" else "in" } > /sys/class/gpio/gpio$number/direction\n"
            runAsRoot(cmd)
        }

    fun registerCallback(callback: GpioCallback) {
        this.callback = callback
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {

                    // Notifier the observer that the state has changed
                    if (_value == null || _value != value) {
                        callback.onValueChanged(value)
                        _value = value
                    }
                }
            }, 0, 250
        )
    }

    fun unregisterCallback() {
        timer.cancel()
        this.callback = null
    }
}
