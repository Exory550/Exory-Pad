package com.exory550.exorypad.ui.components

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit

@Composable
fun DialogTitle(
    id: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(id = id),
        modifier = modifier
    )
}

@Composable
fun DialogText(
    id: Int,
    vararg formatArgs: Any,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(id = id, formatArgs),
        modifier = modifier
    )
}

@Composable
fun DialogButton(
    onClick: () -> Unit,
    id: Int,
    modifier: Modifier = Modifier
) {
    TextButton(onClick = onClick) {
        Text(text = stringResource(id = id))
    }
}

@Composable
fun NoteTitle(
    title: String,
    fontSize: TextUnit,
    fontFamily: FontFamily,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        fontSize = fontSize,
        fontFamily = fontFamily,
        color = color,
        maxLines = 1,
        modifier = modifier
    )
}

@Composable
fun NoteDate(
    date: String,
    fontSize: TextUnit,
    fontFamily: FontFamily,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = date,
        fontSize = fontSize,
        fontFamily = fontFamily,
        color = color,
        maxLines = 1,
        modifier = modifier
    )
}

@Composable
fun NoteContent(
    content: String,
    fontSize: TextUnit,
    fontFamily: FontFamily,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = content,
        fontSize = fontSize,
        fontFamily = fontFamily,
        color = color,
        maxLines = 1,
        modifier = modifier
    )
}
