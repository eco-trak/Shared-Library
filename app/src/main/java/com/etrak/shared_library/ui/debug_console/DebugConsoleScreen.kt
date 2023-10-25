package com.etrak.shared_library.ui.debug_console

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.etrak.shared_library.ui.components.Button
import com.etrak.shared_library.ui.components.Dialog
import com.etrak.shared_library.ui.components.TopBar
import com.etrak.shared_library.ui.theme.SharedLibraryTheme
import com.etrak.shared_library.ui.theme.terminalFontColor
import com.etrak.shared_library.ui.theme.terminalTypography
import com.etrak.shared_library.R
import com.etrak.shared_library.ui.components.ListItem
import kotlinx.coroutines.flow.collect

@Composable
fun Log(log: String) {
    Text(
        text = log,
        color = terminalFontColor,
        style = terminalTypography.body1
    )
}

@Composable
fun Logs(
    logs: List<String>,
    modifier: Modifier = Modifier
) {
    val state = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = state
    ) {
        items(items = logs) { message ->
            Log(log = message)
        }
    }
}

@Composable
fun BottomBar(
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onClearClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            icon = R.drawable.start,
            text = stringResource(R.string.start).uppercase(),
            onClick = onStartClick,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Button(
            icon = R.drawable.stop,
            text = stringResource(R.string.stop).uppercase(),
            onClick = onStopClick,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Button(
            icon = R.drawable.clear,
            text = stringResource(R.string.clear).uppercase(),
            onClick = onClearClick,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Button(
            icon = R.drawable.settings,
            text = stringResource(R.string.settings).uppercase(),
            onClick = onSettingsClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
fun BottomBarPreview() {
    SharedLibraryTheme(darkTheme = true) {
        BottomBar(
            onStartClick = {},
            onStopClick = {},
            onClearClick = {},
            onSettingsClick = {}
        )
    }
}

@Composable
fun DebugConsoleContent(
    onPopBackStack: () -> Unit,
    log: List<String>,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onClearClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopBar(
                icon = R.drawable.bug,
                text = R.string.debug_console,
                onPopBackStack = onPopBackStack,
                modifier = Modifier
            )
        }
    ) {

            Column(
                modifier = Modifier.background(Color.Black)
            ) {
                Logs(
                    logs = log,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                BottomBar(
                    onStartClick = onStartClick,
                    onStopClick = onStopClick,
                    onClearClick = onClearClick,
                    onSettingsClick = onSettingsClick,
                    modifier = Modifier.padding(all = 10.dp)
                )
            }
    }
}

@Preview(widthDp = 500)
@Composable
fun UsbConsoleContentPreview() {
    SharedLibraryTheme(darkTheme = true) {
        DebugConsoleContent(
            onPopBackStack = {},
            log = listOf(
                "<CA01>",
                "<CB09>",
                "<CA13>"
            ),
            onStartClick = {},
            onStopClick = {},
            onClearClick = {},
            onSettingsClick = {}
        )
    }
}

@Composable
fun SettingsDialog(
    onDismissRequest: () -> Unit,
    bufferSize: Int,
    onBufferSizeChange: (value: String) -> Unit,
    onSave: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .padding(
                    start = 10.dp,
                    top = 75.dp,
                    end = 10.dp,
                    bottom = 75.dp
                )
                .width(IntrinsicSize.Max),
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = bufferSize.toString(),
                onValueChange = onBufferSizeChange,
                label = {
                    Text(text = stringResource(id = R.string.buffer_size).uppercase())
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                ListItem(
                    icon = R.drawable.save,
                    text = stringResource(R.string.save),
                    onClick = onSave
                )
            }
        }
    }
}

@Preview(widthDp = 500)
@Composable
fun SettingsDialogPreview() {
    SharedLibraryTheme(darkTheme = true) {
        SettingsDialog(
            onDismissRequest = {},
            bufferSize = 1000,
            onBufferSizeChange = {},
            onSave = {}
        )
    }
}


@Composable
fun UsbConsoleScreen(
    viewModel: DebugConsoleViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showSettings by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.events.collect { event ->
            when (event) {
                is DebugConsoleViewModel.UiEvent.OnError -> Toast.makeText(
                    context,
                    event.e.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    DebugConsoleContent(
        onPopBackStack = {},
        log = viewModel.logs,
        onStartClick = viewModel::onStartClick,
        onStopClick = viewModel::onStopClick,
        onClearClick = viewModel::onClearClick,
        onSettingsClick = {
            showSettings = true
        }
    )

    if (showSettings) {
        SettingsDialog(
            onDismissRequest = {
                showSettings = false
            },
            bufferSize = viewModel.bufferSize,
            onBufferSizeChange = viewModel::onBufferSizeChange,
            onSave = {
                showSettings = false
            }
        )
    }
}
