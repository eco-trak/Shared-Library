package com.etrak.shared_library.scale_service

import com.etrak.core.mc_service.Device
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ScaleEmulator : Device {
    override val messages: Flow<Device.Message> by lazy {
        flow {
            var param = 0
            while (true) {
                emit(
                    Device.Message(
                        code = Code.BoomAngle.value,
                        params = listOf(param.toString())
                    )
                )
                delay(1000)
                param++
            }
        }
    }

    override fun send(msg: Device.Message) {
        TODO("Not yet implemented")
    }

    override fun connect() {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }
}