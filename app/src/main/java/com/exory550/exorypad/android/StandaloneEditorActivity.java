package com.exory550.exorypad.android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.ComponentActivity;
import androidx.activity.compose.setContent;
import com.exory550.exorypad.R;
import com.exory550.exorypad.ui.routes.StandaloneEditorRoute;
import com.exory550.exorypad.viewmodel.ExorypadViewModel;
import org.koin.androidx.viewmodel.ext.android.viewModel;

public class StandaloneEditorActivity extends ComponentActivity {
    private final ExorypadViewModel vm = new ExorypadViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch(intent.getAction()) {
            case Intent.ACTION_MAIN:
                openEditor();
                break;

            case Intent.ACTION_SEND:
                checkPlainText(() -> {
                    String content = getExternalContent();
                    if (content != null) {
                        openEditor(content);
                    } else {
                        externalContentFailed();
                    }
                });
                break;

            case Intent.ACTION_EDIT:
                checkPlainText(() -> {
                    String text = intent.getStringExtra(Intent.EXTRA_TEXT);
                    if (text != null) {
                        openEditor(text);
                    } else {
                        externalContentFailed();
                    }
                });
                break;

            case Intent.ACTION_VIEW:
                checkPlainText(() -> {
                    vm.loadFileFromIntent(intent, file -> {
                        if (file != null) {
                            openEditor(file);
                        } else {
                            externalContentFailed();
                        }
                    });
                });
                break;

            default:
                externalContentFailed();
                break;
        }
    }

    private void checkPlainText(Runnable onSuccess) {
        if (intent.getType().equals("text/plain")) {
            onSuccess.run();
        } else {
            externalContentFailed();
        }
    }

    private String getExternalContent() {
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (text == null) return null;
        
        String subject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
        if (subject == null) return text;
        
        return subject + "\n\n" + text;
    }

    private void externalContentFailed() {
        Toast.makeText(this, R.string.loading_external_file, Toast.LENGTH_LONG).show();
        finish();
    }

    private void openEditor() {
        openEditor("");
    }

    private void openEditor(String initialText) {
        setContent {
            StandaloneEditorRoute(
                initialText = initialText,
                onFinish = () -> finish()
            );
        }
    }
}
