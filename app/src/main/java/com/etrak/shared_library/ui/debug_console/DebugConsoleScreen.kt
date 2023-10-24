package com.etrak.shared_library.ui.debug_console

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.etrak.shared_library.R
import com.etrak.shared_library.ui.components.Button
import com.etrak.shared_library.ui.components.TopBar
import com.etrak.shared_library.ui.theme.SharedLibraryTheme
import com.etrak.shared_library.ui.theme.terminalFontColor
import com.etrak.shared_library.ui.theme.terminalTypography

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
    }
}

@Preview
@Composable
fun BottomBarPreview() {
    SharedLibraryTheme(darkTheme = true) {
        BottomBar(
            onStartClick = {},
            onStopClick = {},
            onClearClick = {}
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
) {
    Scaffold(
        topBar = {
            TopBar(
                icon = R.drawable.bug,
                text = R.string.debug_console,
                onPopBackStack = onPopBackStack
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
            onClearClick = {}
        )
    }
}

@Composable
fun UsbConsoleScreen(
    viewModel: DebugConsoleViewModel = hiltViewModel()
) {
    DebugConsoleContent(
        onPopBackStack = {},
        log = viewModel.logs,
        onStartClick = viewModel::onStartClick,
        onStopClick = viewModel::onStopClick,
        onClearClick = viewModel::onClearClick
    )
}
