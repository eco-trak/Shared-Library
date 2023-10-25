package com.etrak.shared_library.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.etrak.shared_library.ui.theme.*

@Composable
fun Dialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismissRequest
    ) {
        androidx.compose.material.Surface(
            modifier = modifier,
            shape = dialogShape,
            border = BorderStroke(3.dp, if (isSystemInDarkTheme()) darkButtonDividerGradient else lightButtonDividerGradient)
        ) {
            content()
        }
    }
}

@Preview(widthDp = 500)
@Composable
fun DialogPreview() {
    SharedLibraryTheme(darkTheme = true) {
        Dialog(onDismissRequest = { }) {
            Column(
                modifier = Modifier.fillMaxSize(0.75f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Hello World!!")
            }
        }
    }
}