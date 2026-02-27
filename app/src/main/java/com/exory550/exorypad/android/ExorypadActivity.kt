package com.exory550.exorypad.android

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.exory550.exorypad.ui.routes.ExorypadComposeAppRoute
import com.exory550.exorypad.viewmodel.ExorypadViewModel
import com.github.k1rakishou.fsaf.FileChooser
import com.github.k1rakishou.fsaf.callback.FSAFActivityCallbacks
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExorypadActivity: ComponentActivity(), FSAFActivityCallbacks {
    private val vm: ExorypadViewModel by viewModel()
    private val fileChooser: FileChooser = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fileChooser.setCallbacks(this)

        vm.migrateData {
            setContent {
                ExorypadComposeAppRoute(
                    restoredFromState = savedInstanceState != null,
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        vm.deleteDraft()
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) {
            vm.saveDraft()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fileChooser.removeCallbacks()
    }

    override fun fsafStartActivityForResult(intent: Intent, requestCode: Int) {
        when(intent.action) {
            Intent.ACTION_OPEN_DOCUMENT -> intent.type = vm.currentMimeType
            Intent.ACTION_OPEN_DOCUMENT_TREE -> intent.removeExtra(Intent.EXTRA_LOCAL_ONLY)
        }

        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fileChooser.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchKeyShortcutEvent(event: KeyEvent): Boolean {
        return if (event.action == KeyEvent.ACTION_DOWN && event.isCtrlPressed) {
            vm.keyboardShortcutPressed(event.keyCode)
        } else {
            super.dispatchKeyShortcutEvent(event)
        }
    }
}
