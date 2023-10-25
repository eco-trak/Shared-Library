package com.etrak.shared_library.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.etrak.shared_library.R
import com.etrak.shared_library.ui.theme.SharedLibraryTheme

// TODO: Add the beep when the user clicks the composable

@Composable
fun ListItem(
    @DrawableRes icon: Int?,
    text: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(all = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null ) Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            if (icon != null && text != null) Spacer(
                modifier = Modifier.width(5.dp)
            )
            if (text != null) Text(
                text = text.uppercase(),
                modifier = Modifier.weight(1f),
                color = Color.White
            )
        }
    }
}

@Preview(widthDp = 500)
@Composable
fun ListItemPreview() {
    SharedLibraryTheme(darkTheme = true) {
        Row(
            horizontalArrangement = Arrangement.End
        ) {
            ListItem(
                icon = R.drawable.stop,
                text = stringResource(id = R.string.stop),
                onClick = { },
                modifier = Modifier.width(185.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            ListItem(
                icon = R.drawable.start,
                text = stringResource(id = R.string.start),
                onClick = { },
                modifier = Modifier.width(185.dp)
            )
        }
    }
}