package com.example.to_dolist.feature

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.to_dolist.models.Task
import com.example.to_dolist.receive.NotificationBroadcastReceiver
import com.example.to_dolist.utils.AlarmScheduler
import com.example.to_dolist.utils.Constance.KEY_NOTIFICATION_TO_BROADCAST_RECEIVER
import java.util.Calendar

class AlarmSchedulerTask(
    private val context: Context
) : AlarmScheduler{

    private val alarmManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        context.getSystemService(AlarmManager::class.java)
    } else {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    override fun scheduler(item: Task) {
        val calendar = Calendar.getInstance()
        calendar.set(item.year!!, item.month!! - 1, item.day!!, item.hour!!, item.minute!!, 0)
        val currentCalendar = Calendar.getInstance()
        if (calendar.timeInMillis - currentCalendar.timeInMillis > 0) {
            val intent = Intent(context, NotificationBroadcastReceiver::class.java).apply {
                putExtra(KEY_NOTIFICATION_TO_BROADCAST_RECEIVER, item)
            }
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                PendingIntent.getBroadcast(
                    context,
                    item.id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
    }

    override fun cancel(item: Task) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.id,
                Intent(context, NotificationBroadcastReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}