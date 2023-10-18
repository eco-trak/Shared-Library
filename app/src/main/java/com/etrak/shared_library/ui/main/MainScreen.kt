package com.etrak.shared_library.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Cab. Angle:")
            Text(
                text = viewModel.cabAngle.toString(),
                fontSize = 84.sp
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Boom. Angle:")
            Text(
                text = viewModel.boomAngle.toString(),
                fontSize = 84.sp
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Calib. UI Cab. Angle:")
            Text(
                text = viewModel.calibUICabAngle.toString(),
                fontSize = 84.sp
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Process Angle:")
            Text(
                text = viewModel.processAngle.toString(),
                fontSize = 84.sp
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Calib. UI Boom Angle:")
            Text(
                text = viewModel.calibUIBoomAngle.toString(),
                fontSize = 84.sp
            )
        }
    }
}