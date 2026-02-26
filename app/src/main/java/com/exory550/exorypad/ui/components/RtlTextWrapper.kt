package com.exory550.exorypad.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import java.text.Bidi

@Composable
fun RtlTextWrapper(
    text: String,
    rtlLayout: Boolean,
    content: @Composable () -> Unit
) {
    val flags = if (rtlLayout) {
        Bidi.DIRECTION_DEFAULT_RIGHT_TO_LEFT
    } else {
        Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT
    }

    val bidi = Bidi(text, flags)

    val layoutDirection = if (text.isBlank()) {
        when (rtlLayout) {
            true -> LayoutDirection.Rtl
            false -> LayoutDirection.Ltr
        }
    } else {
        when (bidi.baseIsLeftToRight()) {
            true -> LayoutDirection.Ltr
            false -> LayoutDirection.Rtl
        }
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection,
        content = content
    )
}
