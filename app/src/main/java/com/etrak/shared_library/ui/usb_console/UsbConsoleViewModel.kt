package com.etrak.shared_library.ui.usb_console

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etrak.core.mc_service.Device
import com.etrak.core.mc_service.McManager
import com.etrak.shared_library.scale_service.Scale
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsbConsoleViewModel @Inject constructor(

    private val mcManager: McManager

) : ViewModel() {

    companion object {
        private const val MAX_EVENTS = 10000
    }

    private val messages = mcManager.messages.shareIn(viewModelScope, SharingStarted.Eagerly, 0)
    private val _log = mutableStateListOf<Device.Message>()
    val log: List<Device.Message> get() = _log

    init {
        viewModelScope.launch {
            messages.collect { msg ->
                if (_log.size == MAX_EVENTS)
                    _log.removeFirst()
                _log += msg
            }
        }
    }
}