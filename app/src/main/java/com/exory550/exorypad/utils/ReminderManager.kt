package com.exory550.exorypad.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.exory550.exorypad.android.ReminderReceiver
import java.util.Date

fun Context.setReminder(noteId: Long, title: String, time: Date) {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(this, ReminderReceiver::class.java).apply {
        putExtra("noteId", noteId)
        putExtra("title", title)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        this, noteId.toInt(), intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time.time, pendingIntent)
}

fun Context.cancelReminder(noteId: Long) {
    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(this, ReminderReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        this, noteId.toInt(), intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
}
