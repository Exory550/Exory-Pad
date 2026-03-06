package com.exory550.exorypad.ui.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.exory550.exorypad.BuildConfig
import com.exory550.exorypad.R
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

private val buildYear: Int get() {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Denver")).apply {
        timeInMillis = BuildConfig.TIMESTAMP
    }
    return calendar.get(Calendar.YEAR)
}

@Composable
fun DeleteDialog(
    isMultiple: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val title = if (isMultiple) R.string.dialog_delete_button_title_plural else R.string.dialog_delete_button_title
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { DialogTitle(id = title) },
        text = { DialogText(id = R.string.dialog_are_you_sure) },
        confirmButton = { DialogButton(onClick = onConfirm, id = R.string.action_delete) },
        dismissButton = { DialogButton(onClick = onDismiss, id = R.string.action_cancel) }
    )
}

@Composable
fun AboutDialog(
    onDismiss: () -> Unit,
    checkForUpdates: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { DialogTitle(id = R.string.dialog_about_title) },
        text = { DialogText(id = R.string.dialog_about_message, buildYear) },
        confirmButton = { DialogButton(onClick = onDismiss, id = R.string.action_close) },
        dismissButton = { DialogButton(onClick = checkForUpdates, id = R.string.check_for_updates) }
    )
}

@Composable
fun SaveDialog(
    onConfirm: () -> Unit,
    onDiscard: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { DialogTitle(id = R.string.dialog_save_button_title) },
        text = { DialogText(id = R.string.dialog_save_changes) },
        confirmButton = { DialogButton(onClick = onConfirm, id = R.string.action_save) },
        dismissButton = { DialogButton(onClick = onDiscard, id = R.string.action_discard) }
    )
}

@Composable
fun FirstRunDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { DialogTitle(id = R.string.app_name) },
        text = { DialogText(id = R.string.first_run) },
        confirmButton = { DialogButton(onClick = onDismiss, id = R.string.action_close) },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    )
}

@Composable
fun FirstViewDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { DialogTitle(id = R.string.app_name) },
        text = { DialogText(id = R.string.first_view) },
        confirmButton = { DialogButton(onClick = onDismiss, id = R.string.action_close) }
    )
}

@Composable
fun LabelDialog(
    currentLabel: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var label by remember { mutableStateOf(currentLabel) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { DialogTitle(id = R.string.action_label) },
        text = {
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                placeholder = { Text(stringResource(R.string.label_hint)) },
                singleLine = true
            )
        },
        confirmButton = { DialogButton(onClick = { onConfirm(label) }, id = R.string.action_save) },
        dismissButton = { DialogButton(onClick = onDismiss, id = R.string.action_cancel) }
    )
}

@Composable
fun ReminderDialog(
    onConfirm: (Date) -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    var hour by remember { mutableStateOf(8) }
    var minute by remember { mutableStateOf(0) }
    var daysFromNow by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { DialogTitle(id = R.string.action_set_reminder) },
        text = {
            androidx.compose.foundation.layout.Column {
                Text("Hari dari sekarang: $daysFromNow")
                Slider(
                    value = daysFromNow.toFloat(),
                    onValueChange = { daysFromNow = it.toInt() },
                    valueRange = 0f..30f,
                    steps = 29
                )
                Text("Jam: $hour:${minute.toString().padStart(2, '0')}")
                Slider(
                    value = hour.toFloat(),
                    onValueChange = { hour = it.toInt() },
                    valueRange = 0f..23f,
                    steps = 22
                )
                Slider(
                    value = minute.toFloat(),
                    onValueChange = { minute = it.toInt() },
                    valueRange = 0f..59f,
                    steps = 58
                )
            }
        },
        confirmButton = {
            DialogButton(
                onClick = {
                    val cal = Calendar.getInstance().apply {
                        add(Calendar.DAY_OF_YEAR, daysFromNow)
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                    }
                    onConfirm(cal.time)
                },
                id = R.string.action_save
            )
        },
        dismissButton = { DialogButton(onClick = onCancel, id = R.string.action_cancel) }
    )
}
