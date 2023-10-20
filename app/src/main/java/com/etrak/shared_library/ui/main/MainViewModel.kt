package com.etrak.shared_library.ui.main

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etrak.shared_library.scale_service.Scale
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "eco-trak MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(

    private val scale: Scale

): ViewModel() {

    // Scale events
    private val scaleEvents = scale.events.shareIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly
    )

    // Scale variables
    var cabAngle by mutableStateOf(0)
        private set
    var boomAngle by mutableStateOf(0)
        private set
    var calibUICabAngle by mutableStateOf(0)
        private set
    var processAngle by mutableStateOf(0)
        private set
    var calibUIBoomAngle by mutableStateOf(0)
        private set

    // Collect scale events
    init {

        scale.start()

        viewModelScope.launch {
            scaleEvents.collect { event ->
                when (event) {
                    is Scale.Event.OnCabAngle -> cabAngle = event.angle
                    is Scale.Event.OnBoomAngle -> boomAngle = event.angle
                    is Scale.Event.OnCalibUICabAngle -> calibUICabAngle = event.angle
                    is Scale.Event.OnProcessAngle -> processAngle = event.angle
                    is Scale.Event.OnCalibUIBoomAngle -> calibUIBoomAngle = event.angle
                    is Scale.Event.OnUnknown -> Log.d(TAG, "Unknown message: ${event.msg}")
                }
            }
        }
    }
}