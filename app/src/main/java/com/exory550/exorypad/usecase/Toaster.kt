package com.exory550.exorypad.usecase

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

interface Toaster {
    suspend fun toast(@StringRes text: Int)

    suspend fun toastIf(
        condition: Boolean,
        @StringRes text: Int,
        block: () -> Unit
    )
}

private class ToasterImpl(
    private val context: Context
): Toaster {
    override suspend fun toast(@StringRes text: Int) = withContext(Dispatchers.Main) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    override suspend fun toastIf(
        condition: Boolean,
        @StringRes text: Int,
        block: () -> Unit
    ) = if (condition) toast(text) else block()
}

val toasterModule = module {
    single<Toaster> {
        ToasterImpl(androidContext())
    }
}
