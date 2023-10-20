package com.etrak.shared_library

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.etrak.core.shutdown_service.ShutdownManager
import com.etrak.core.shutdown_service.ShutdownService.Companion.DEFAULT_DURATION
import com.etrak.shared_library.ui.shutdown_service.ShutdownSequence
import com.etrak.shared_library.ui.main.MainScreen
import com.etrak.shared_library.ui.theme.SharedLibraryTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.compose.runtime.*
import com.etrak.core.mc_service.McManager
import com.etrak.core.mc_service.McService
import com.etrak.shared_library.scale_service.DebugDialog

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var mcManager: McManager

    @Inject
    lateinit var shutdownManager: ShutdownManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request permission to post notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                ),
                0
            )
        }

        setContent {
            SharedLibraryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    // Observe the mc manager
                    val showDebugDialog by mcManager.showDebugDialog.collectAsState(initial = false)

                    // Observe the shutdown manager
                    val showCountdown by shutdownManager.showCountdownSequence.collectAsState(initial = false)
                    val countdown by shutdownManager.countdown.collectAsState(initial = DEFAULT_DURATION)

                    // Show the main screen
                    MainScreen()

                    // Show debug window
                    if (showDebugDialog)
                        DebugDialog(
                            onSetEmulatorMode = {
                                mcManager.setMode(McService.Mode.Emulator)
                            }
                        )

                    // Show the countdown dialog on top
                    if (showCountdown) {
                        ShutdownSequence(
                            onCancelClick = {
                                shutdownManager.cancelShutdownSequence()
                            },
                            countdown = countdown
                        )
                    }
                }
            }
        }
    }
}
