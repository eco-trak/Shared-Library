package com.etrak.shared_library.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etrak.shared_library.R
import com.etrak.shared_library.ui.theme.*

// TODO: Add the beep when the user clicks the composable

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .clickable(enabled = enabled) {
                onClick()
            }
            .alpha(if (enabled) 1f else 0.3f)
            .background(
                brush = if (isSystemInDarkTheme()) darkButtonBackgroundGradient else lightButtonBackgroundGradient,
                shape = buttonShape
            )
            .border(
                width = 1.dp,
                brush = if (isSystemInDarkTheme()) darkButtonBorderGradient else lightButtonBorderGradient,
                shape = buttonShape
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
        Divider(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(3.dp)
                .background(
                    brush = if (isSystemInDarkTheme()) darkButtonDividerGradient else lightButtonDividerGradient
                )
        )
    }
}

@Preview
@Composable
fun ButtonPreview() {
    SharedLibraryTheme(darkTheme = true) {
        Button(
            onClick = { },
            modifier = Modifier.alpha(0.35f)
        ) {
            Row(
                modifier = Modifier.padding(all = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.cancel),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Text(text = stringResource(id = R.string.cancel).uppercase())
            }
        }
    }
}

@Composable
fun Button(
    @DrawableRes icon: Int?,
    text: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled
    ) {
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .padding(all = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (icon != null)
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            if (text != null)
                Text(
                    text = text.uppercase(),
                    color = Color.White
                )
        }
    }
}

@Preview
@Composable
fun ButtonPreview2() {
    SharedLibraryTheme(darkTheme = true) {
        Button(
            icon = R.drawable.print,
            text = stringResource(id = R.string.print),
            onClick = { }
        )
    }
}

@Preview
@Composable
fun ButtonPreview3() {
    SharedLibraryTheme(darkTheme = true) {
        Button(
            icon = null,
            text = stringResource(id = R.string.print),
            onClick = { }
        )
    }
}

@Preview
@Composable
fun ButtonPreview4() {
    SharedLibraryTheme(darkTheme = true) {
        Button(
            icon = R.drawable.print,
            text = null,
            onClick = { }
        )
    }
}

@Preview
@Composable
fun ButtonPreview5() {
    SharedLibraryTheme(darkTheme = true) {
        Button(
            icon = null,
            text = null,
            onClick = { },
            modifier = Modifier
                .width(75.dp)
                .height(75.dp)
        )
    }
}

@Composable
fun Button(
    @DrawableRes icon: Int?,
    text: String?,
    badge: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (badge != null) {
        Button(
            onClick = onClick,
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .padding(all = 10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BadgedBox(
                    badge = {
                        Badge(
                            backgroundColor = if (isSystemInDarkTheme()) darkBadgeBackgroundColor else lightBadgeBackgroundColor,
                            contentColor = if (isSystemInDarkTheme()) darkBadgeContentColor else lightBadgeContentColor,
                        ) {
                            Text(
                                text = badge.uppercase(),
                                fontSize = 10.sp
                            )
                        }
                    }
                ) {
                    if (icon != null)
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                }
                if (text != null)
                    Text(
                        text = text.uppercase(),
                        color = Color.White
                    )
            }
        }
    }
    else {
        Button(
            icon = icon,
            text = text,
            onClick = onClick,
            modifier = modifier
        )
    }
}

@Preview
@Composable
fun ButtonPreview6() {
    SharedLibraryTheme(darkTheme = true) {
        Button(
            icon = R.drawable.print,
            text = stringResource(R.string.print),
            badge = "1",
            onClick = { }
        )
    }
}

@Preview
@Composable
fun ButtonPreview9() {
    SharedLibraryTheme(darkTheme = true) {
        Button(
            icon = R.drawable.print,
            text = null,
            badge = "1",
            onClick = { },
            modifier = Modifier.width(75.dp)
        )
    }
}

