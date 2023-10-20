package com.etrak.shared_library.scale_service

import com.etrak.core.mc_service.Device
import com.etrak.core.mc_service.McManager
import com.etrak.core.mc_service.McService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class Code(val value: String) {
    BoomAngle(value = "AD08"),
    CabAngle(value = "AD09"),
    CalibUICabAngle(value = "AD10"),
    ProcessAngle(value = "AD11"),
    CalibUIBoomAngle(value = "AD38")
}

class Scale(

    private val mcManager: McManager

) {
    sealed class Event {
        data class OnBoomAngle(val angle: Int) : Event()
        data class OnCabAngle(val angle: Int) : Event()
        data class OnCalibUICabAngle(val angle: Int) : Event()
        data class OnProcessAngle(val angle: Int) : Event()
        data class OnCalibUIBoomAngle(val angle: Int) : Event()
        data class OnUnknown(val msg: Device.Message) : Event()
    }

//    val notifications by mcManager::notifications
//    val showDebugDialog by mcManager::showDebugDialog

    val events: Flow<Event> by lazy {
        mcManager.messages.map { message ->
            when (message.code) {

                Code.BoomAngle.value -> Event.OnBoomAngle(message.params[0].toInt())
                Code.CabAngle.value -> Event.OnCabAngle(message.params[0].toInt())
                Code.CalibUICabAngle.value -> Event.OnCalibUICabAngle(message.params[0].toInt())
                Code.ProcessAngle.value -> Event.OnProcessAngle(message.params[0].toInt())
                Code.CalibUIBoomAngle.value -> Event.OnCalibUIBoomAngle(message.params[0].toInt())

                else -> Event.OnUnknown(message)
            }
        }
    }

//    fun runEmulator() = mcManager.setMode(McService.Mode.Emulator)

//    fun start() {
//        mcManager.send(Device.Message(code = "CD00", params = emptyList()))
//    }
}