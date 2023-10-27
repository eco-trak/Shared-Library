package com.etrak.shared_library.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.etrak.shared_library.R
import com.etrak.shared_library.ui.theme.*
import org.intellij.lang.annotations.JdkConstants.FontStyle

@Composable
fun Dialog(
    onDismissRequest: () -> Unit,
    @DrawableRes icon: Int,
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismissRequest
    ) {
        androidx.compose.material.Surface(
            modifier = modifier,
            shape = dialogShape,
            color = MaterialTheme.colors.background,
            border = BorderStroke(3.dp, if (isSystemInDarkTheme()) darkButtonDividerGradient else lightButtonDividerGradient)
        ) {
            Column(modifier = Modifier.padding(all = 30.dp)) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = title.uppercase(),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = dialogTitleTextStyle
                    )
                }
                Column(
                    modifier = Modifier.padding(top = 50.dp, bottom = 25.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Preview(widthDp = 500)
@Composable
fun DialogPreview() {
    SharedLibraryTheme(darkTheme = true) {
        Dialog(
            onDismissRequest = { },
            icon = R.drawable.bug,
            title = stringResource(id = R.string.debug_console)
        ) {
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