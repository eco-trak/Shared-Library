package com.etrak.core.mc_service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class StandbyMode : Device {

    override fun connect() {
        TODO("Not yet implemented")
    }

    override val messages: Flow<Device.Message>
        get() = emptyFlow()

    override fun send(msg: Device.Message) {
        TODO("Not yet implemented")
    }

    override fun getMessage(): Device.Message {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }
}