package com.exory550.exorypad.ui.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exory550.exorypad.R
import com.exory550.exorypad.ui.components.RtlTextWrapper
import com.exory550.exorypad.ui.previews.EditNotePreview
import kotlinx.coroutines.delay
import java.text.DateFormat
import java.util.Date

private fun String.toTextFieldState() = TextFieldState(
    initialText = this,
    initialSelection = TextRange(length)
)

@Composable
fun EditNoteContent(
    text: String,
    title: String = "",
    date: Date = Date(),
    baseTextStyle: TextStyle = TextStyle(),
    isLightTheme: Boolean = true,
    isPrinting: Boolean = false,
    waitForAnimation: Boolean = false,
    rtlLayout: Boolean = false,
    offset: Offset? = null,
    onTextChanged: (String) -> Unit = {},
    onTitleChanged: (String) -> Unit = {},
) {
    val textStyle = if (isPrinting) baseTextStyle.copy(color = Color.Black) else baseTextStyle

    val titleStyle = textStyle.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold)

    val dateStyle = textStyle.copy(
        fontSize = 12.sp,
        color = if (isLightTheme) Color.Gray else Color.DarkGray
    )

    val hintColor = if (isLightTheme) Color.Gray else Color.DarkGray
    val dateText = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(date)

    val focusRequester = remember { FocusRequester() }
    var value by remember { mutableStateOf(text.toTextFieldState()) }
    var titleValue by rememberSaveable { mutableStateOf(title) }

    LaunchedEffect(text) {
        if (text != value.text) value = text.toTextFieldState()
    }

    LaunchedEffect(title) {
        if (title != titleValue) titleValue = title
    }

    val brush = SolidColor(
        value = when {
            isPrinting -> Color.Transparent
            isLightTheme -> Color.Black
            else -> Color.White
        }
    )

    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        RtlTextWrapper(titleValue, rtlLayout) {
            BasicTextField(
                value = TextFieldValue(titleValue, TextRange(titleValue.length)),
                onValueChange = {
                    titleValue = it.text
                    onTitleChanged(it.text)
                },
                textStyle = titleStyle,
                cursorBrush = brush,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                decorationBox = { innerTextField ->
                    if (titleValue.isEmpty()) {
                        BasicText(
                            text = stringResource(id = R.string.hint_title),
                            style = titleStyle.copy(color = hintColor)
                        )
                    }
                    innerTextField()
                }
            )
        }

        BasicText(
            text = dateText,
            style = dateStyle,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
        )

        RtlTextWrapper(text, rtlLayout) {
            BasicTextField(
                state = value,
                outputTransformation = OutputTransformation { onTextChanged(value.text.toString()) },
                textStyle = textStyle,
                cursorBrush = brush,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                onTextLayout = { layoutResult.value = it() },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .fillMaxSize()
                    .focusRequester(focusRequester),
                decorator = { innerTextField ->
                    if (value.text.isEmpty()) {
                        BasicText(
                            text = stringResource(id = R.string.hint_note),
                            style = textStyle.copy(color = hintColor)
                        )
                    }
                    innerTextField()
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        if (waitForAnimation) delay(200)

        offset?.let { offset ->
            layoutResult.value?.let { layoutResult ->
                val position = layoutResult.getOffsetForPosition(offset)
                value.edit { selection = TextRange(position) }
            }
        }

        focusRequester.requestFocus()
    }
}

@Preview
@Composable
fun EditNoteContentPreview() = EditNotePreview()
