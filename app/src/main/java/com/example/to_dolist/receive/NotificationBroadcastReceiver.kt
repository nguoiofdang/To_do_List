package com.example.to_dolist.receive

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.to_dolist.R
import com.example.to_dolist.models.Task
import com.example.to_dolist.ui.MainActivity
import com.example.to_dolist.utils.Constance
import com.example.to_dolist.utils.Constance.CHANEL_ID_TODO_REMINDER
import com.example.to_dolist.utils.Constance.ID_GROUP
import com.example.to_dolist.utils.Constance.KEY_NOTIFICATION_TO_BROADCAST_RECEIVER
import com.example.to_dolist.utils.Constance.TAG_INTENT_TASK

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val task = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getSerializableExtra(
                KEY_NOTIFICATION_TO_BROADCAST_RECEIVER,
                Task::class.java
            )!!
        } else {
            intent?.getSerializableExtra(KEY_NOTIFICATION_TO_BROADCAST_RECEIVER) as Task
        }
        context?.let {
            pushNotification(it, task)
        }
    }

    private fun pushNotification(context: Context, task: Task) {

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(TAG_INTENT_TASK, task)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANEL_ID_TODO_REMINDER)
            .setSmallIcon(R.drawable.ic_app)
            .setContentTitle(task.titleTask)
            .setContentText(task.description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(task.description))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setGroup(ID_GROUP)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(task.id, notification.build())
        }
    }
}