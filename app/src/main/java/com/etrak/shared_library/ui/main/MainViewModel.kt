package com.etrak.shared_library.ui.main

import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.etrak.shared_library.scale_service.Scale
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    // Collect scale events
    init {
        viewModelScope.launch {
            scaleEvents.collect { event ->
                when (event) {
                    is Scale.Event.OnCabAngle -> {
                        cabAngle = event.angle
                    }
                    else -> Unit
                }
            }
        }
    }

    // https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories
    companion object {
        fun provideFactory(
            scale: Scale,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return MainViewModel(
                        scale
                    ) as T
                }
            }
    }
}