package com.exory550.exorypad.android;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import androidx.activity.ComponentActivity;
import androidx.activity.compose.setContent;
import com.exory550.exorypad.ui.routes.ExorypadComposeAppRoute;
import com.exory550.exorypad.viewmodel.ExorypadViewModel;
import com.github.k1rakishou.fsaf.FileChooser;
import com.github.k1rakishou.fsaf.callback.FSAFActivityCallbacks;
import org.koin.android.ext.android.get;
import org.koin.androidx.viewmodel.ext.android.viewModel;

public class ExorypadActivity extends ComponentActivity implements FSAFActivityCallbacks {
    private final ExorypadViewModel vm = new ExorypadViewModel();
    private final FileChooser fileChooser = get();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileChooser.setCallbacks(this);

        vm.migrateData(() -> {
            setContent {
                ExorypadComposeAppRoute(
                    restoredFromState = savedInstanceState != null
                );
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        vm.deleteDraft();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isChangingConfigurations) {
            vm.saveDraft();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fileChooser.removeCallbacks();
    }

    @Override
    public void fsafStartActivityForResult(Intent intent, int requestCode) {
        switch(intent.getAction()) {
            case Intent.ACTION_OPEN_DOCUMENT:
                intent.setType(vm.getCurrentMimeType());
                break;
            case Intent.ACTION_OPEN_DOCUMENT_TREE:
                intent.removeExtra(Intent.EXTRA_LOCAL_ONLY);
                break;
        }

        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fileChooser.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.isCtrlPressed()) {
            return vm.keyboardShortcutPressed(event.getKeyCode());
        }
        return super.dispatchKeyShortcutEvent(event);
    }
}
