package com.exory550.exorypad.ui.components

import androidx.annotation.StringRes
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.exory550.exorypad.R

@Composable
fun AppBarText(text: String) {
    Text(
        text = text,
        color = Color.White,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun DialogTitle(@StringRes id: Int) {
    Text(
        text = stringResource(id),
        fontWeight = FontWeight.W500
    )
}

@Composable
fun DialogText(@StringRes id: Int, vararg formatArgs: Any) {
    Text(
        text = stringResource(id, *formatArgs)
    )
}

@Composable
fun DialogButton(onClick: () -> Unit, @StringRes id: Int) {
    TextButton(onClick) {
        Text(
            text = stringResource(id).uppercase(),
            color = colorResource(id = R.color.primary)
        )
    }
}
