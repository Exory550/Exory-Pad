package com.exory550.exorypad.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.net.Uri
import android.util.Base64
import androidx.datastore.preferences.preferencesDataStore
import com.exory550.exorypad.BuildConfig
import com.exory550.exorypad.R
import com.exory550.exorypad.model.ReleaseType

fun Context.checkForUpdates() {
    val id = BuildConfig.APPLICATION_ID
    val url = when(releaseType) {
        ReleaseType.PlayStore -> {
            if(isPlayStoreInstalled)
                "https://play.google.com/store/apps/details?id=$id"
            else
                "https://github.com/exory550/Exorypad/releases"
        }
        ReleaseType.Amazon -> "https://www.amazon.com/gp/mas/dl/android?p=$id"
        ReleaseType.FDroid -> "https://f-droid.org/repository/browse/?fdid=$id"
        ReleaseType.Unknown -> ""
    }

    try {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    } catch (ignored: ActivityNotFoundException) {}
}

fun Context.showShareSheet(text: String) = try {
    startActivity(
        Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            },
            getString(R.string.send_to)
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    )
} catch (e: Exception) {
    e.printStackTrace()
}

val Context.dataStore by preferencesDataStore("settings")

@Suppress("Deprecation")
private val Context.isPlayStoreInstalled get() = try {
    packageManager.getPackageInfo("com.android.vending", 0)
    true
} catch(e: PackageManager.NameNotFoundException) {
    false
}

private val Context.releaseType: ReleaseType
    @Suppress("Deprecation", "PackageManagerGetSignatures")
    get() {
        val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        for(enum in ReleaseType.entries) {
            try {
                val enumSignature = Signature(Base64.decode(enum.signature, Base64.DEFAULT))
                info.signatures?.let { signatures ->
                    for(signature in signatures) {
                        if(signature == enumSignature) return enum
                    }
                }
            } catch (ignored: Exception) {}
        }

        return ReleaseType.Unknown
    }
