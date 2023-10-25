package com.etrak.shared_library.ui.debug_console

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etrak.core.mc_service.McManager
import com.etrak.shared_library.scale_service.Scale
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebugConsoleViewModel @Inject constructor(

    private val mcManager: McManager,
    private val scale: Scale

) : ViewModel() {

    sealed class UiEvent {
        data class OnError(val e: Exception) : UiEvent()
    }

    enum class Tab { Message, Event, None }

    // Communication channel used to communicate with the composable
    private val _events = Channel<UiEvent>()
    val events = _events.receiveAsFlow()

    var bufferSize by mutableStateOf(100)
        private set

    // Tab
    private val currentTab = MutableStateFlow(Tab.Message)
    private val previousTab = MutableStateFlow(Tab.None)

    // Flow
    @OptIn(ExperimentalCoroutinesApi::class)
    private val flow = currentTab.flatMapLatest { tab ->
        when (tab) {
            Tab.Message -> mcManager.messages.map { it.toString() }
            Tab.Event -> scale.events.map { it.toString() }
            Tab.None -> emptyFlow()
        }
    }.shareIn(viewModelScope, SharingStarted.Eagerly)

    // Logs
    private val _logs = mutableStateListOf<String>()
    val logs: List<String> get() = _logs

    init {
        viewModelScope.launch {
            flow.collect { msg ->
                if (_logs.size == bufferSize)
                    _logs.removeFirst()
                _logs += msg
            }
        }
    }

    fun onStartClick() {
        currentTab.value = previousTab.value
    }
    fun onStopClick() {
        if (currentTab.value != Tab.None)
            previousTab.value = currentTab.value
        currentTab.value = Tab.None
    }
    fun onClearClick() {
        _logs.clear()
    }
    fun onBufferSizeChange(value: String) {
        try {
            val bufferSize = value.toInt()
            if (bufferSize > 0) {

                onStopClick()
                onCleared()
                this.bufferSize = bufferSize
                onStartClick()

            }
        }
        catch (e: Exception) {
            viewModelScope.launch {
                _events.send(UiEvent.OnError(e))
            }
        }
    }
}

