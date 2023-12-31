package com.etrak.shared_library.scale_service

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etrak.shared_library.R

@Composable
fun DebugDialog(
    onRunEmulator: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        buttons = {
            Column(
                modifier = Modifier.padding(all = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.debug),
                    fontSize = 72.sp
                )
                Button(onClick = onRunEmulator) {
                    Text(text = stringResource(id = R.string.set_emulator_mode))
                }
            }
        }
    )
}