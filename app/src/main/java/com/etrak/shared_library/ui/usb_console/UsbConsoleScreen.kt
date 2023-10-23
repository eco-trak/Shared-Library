package com.etrak.shared_library.ui.usb_console

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.etrak.core.mc_service.Device
import com.etrak.shared_library.ui.theme.terminalFontColor
import com.etrak.shared_library.ui.theme.terminalTypography

@Composable
fun Event(message: Device.Message) {
    Text(
        text = message.toString(),
        color = terminalFontColor,
        style = terminalTypography.body1
    )
}

@Composable
fun Events(
    messages: List<Device.Message>,
    modifier: Modifier = Modifier
) {
    val state = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = state
    ) {
        items(items = messages) { message ->
            Event(message = message)
        }
    }
}


@Composable
fun UsbConsoleScreen(
    viewModel: UsbConsoleViewModel = hiltViewModel()
) {
    val messages = viewModel.log

    Column(
        modifier = Modifier.background(Color.Black)
    ) {
        Events(
            messages = messages,
            modifier = Modifier.weight(1f)
        )
    }
}