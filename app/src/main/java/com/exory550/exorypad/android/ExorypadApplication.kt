package com.exory550.exorypad.android

import android.app.Application
import com.exory550.exorypad.di.exorypadModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class ExorypadApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ExorypadApplication)
            modules(exorypadModule)
        }
    }
}
