package com.etrak.shared_library.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.etrak.shared_library.R
import com.etrak.shared_library.ui.theme.SharedLibraryTheme

@Composable
fun TopBar(
    @DrawableRes icon: Int,
    @StringRes text: Int,
    modifier: Modifier = Modifier,
    onPopBackStack: () -> Unit
) {
    Row(
        modifier = modifier.padding(start = 10.dp, top = 25.dp, end = 10.dp, bottom = 25.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = stringResource(id = text).uppercase(),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h1
        )
        Icon(
            painter = painterResource(id = R.drawable.back),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clickable {
                    onPopBackStack()
                }
        )
    }
}

@Preview(widthDp = 500)
@Composable
fun TopBarPreview() {
    SharedLibraryTheme(darkTheme = true) {
        TopBar(
            onPopBackStack = {},
            icon = R.drawable.bug,
            text = R.string.debug_console
        )
    }
}

@Preview(widthDp = 500)
@Composable
fun TopBarPreview2() {
    SharedLibraryTheme(darkTheme = true) {
        TopBar(
            icon = R.drawable.bug,
            text = R.string.debug_console,
            onPopBackStack = {}
        )
    }
}