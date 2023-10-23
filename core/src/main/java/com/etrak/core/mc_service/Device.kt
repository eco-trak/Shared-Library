package com.etrak.core.mc_service

import kotlinx.coroutines.flow.Flow

interface Device {

    data class Message(val code: String, val params: List<String>) {
        override fun toString()= "<$code${params.joinToString(",")}>"
    }

    val messages: Flow<Message>
    fun send(msg: Message)
    fun connect()
    fun disconnect()
}

