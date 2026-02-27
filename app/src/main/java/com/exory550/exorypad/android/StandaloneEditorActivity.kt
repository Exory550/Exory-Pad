package com.exory550.exorypad.android

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.exory550.exorypad.R
import com.exory550.exorypad.ui.routes.StandaloneEditorRoute
import com.exory550.exorypad.viewmodel.ExorypadViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class StandaloneEditorActivity: ComponentActivity() {
    private val vm: ExorypadViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when(intent.action) {
            Intent.ACTION_MAIN -> openEditor()

            Intent.ACTION_SEND -> checkPlainText {
                getExternalContent()?.let(::openEditor) ?: externalContentFailed()
            }

            Intent.ACTION_EDIT -> checkPlainText {
                intent.getStringExtra(Intent.EXTRA_TEXT)?.let(::openEditor) ?: externalContentFailed()
            }

            Intent.ACTION_VIEW -> checkPlainText {
                vm.loadFileFromIntent(intent) { file ->
                    file?.let(::openEditor) ?: externalContentFailed()
                }
            }

            else -> externalContentFailed()
        }
    }

    private fun checkPlainText(onSuccess: () -> Unit) =
        if (intent.type == "text/plain") onSuccess() else externalContentFailed()

    private fun getExternalContent(): String? {
        val text = intent.getStringExtra(Intent.EXTRA_TEXT) ?: return null
        val subject = intent.getStringExtra(Intent.EXTRA_SUBJECT) ?: return text
        return "$subject\n\n$text".trim()
    }

    private fun externalContentFailed() = run {
        Toast.makeText(this, R.string.loading_external_file, Toast.LENGTH_LONG).show()
        finish()
    }

    private fun openEditor(initialText: String = "") {
        setContent {
            StandaloneEditorRoute(
                initialText = initialText
            ) { finish() }
        }
    }
}
