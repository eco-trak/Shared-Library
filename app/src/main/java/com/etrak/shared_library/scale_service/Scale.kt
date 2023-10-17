package com.etrak.shared_library.scale_service

import com.etrak.core.mc_service.Device
import com.etrak.core.mc_service.McManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Scale(

    private val mcManager: McManager

) {
    sealed class Event {
        data class OnCabAngle(val angle: Int) : Event()
        data class OnUnknown(val msg: Device.Message) : Event()
    }

    val events: Flow<Event> by lazy {
        mcManager.messages.map { message ->
            when (message.code) {

                "AD38" -> Event.OnCabAngle(message.params[0].toInt())

                else -> Event.OnUnknown(message)
            }
        }
    }

    fun start() = mcManager.send(Device.Message(code = "AD00", params = emptyList()))
}