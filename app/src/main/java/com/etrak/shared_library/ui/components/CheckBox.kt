package com.etrak.shared_library.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CheckBox(
    text: String,
    checked: Boolean,
    onCheckedChange: (checked: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .width(16.dp)
                .fillMaxHeight()
        )
        Text(text = text)
        Spacer(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier
                .fillMaxHeight()
        )
        Spacer(
            modifier = Modifier
                .width(16.dp)
                .fillMaxHeight()
        )
    }
}