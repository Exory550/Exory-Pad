package com.exory550.exorypad.usecase

import android.content.Context
import android.content.res.Configuration
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

enum class ColorScheme(val stringValue: String) {
    Light("light"),
    Dark("dark")
}

interface SystemTheme {
    val colorScheme: ColorScheme
}

private class SystemThemeImpl(
    val context: Context
): SystemTheme {
    override val colorScheme: ColorScheme get() {
        val configuration = context.resources.configuration
        return when (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> ColorScheme.Dark
            else -> ColorScheme.Light
        }
    }
}

val systemThemeModule = module {
    single<SystemTheme> {
        SystemThemeImpl(androidContext())
    }
}
