package com.example.to_dolist.applications

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.to_dolist.R
import com.example.to_dolist.utils.Constance.CHANEL_ID_PIN_REMINDER
import com.example.to_dolist.utils.Constance.CHANEL_ID_TODO_REMINDER

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChanelID()
    }

    private fun createNotificationChanelID() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val nameChanel1 = getString(R.string.chanel_name_pin_reminder)
            val descriptionTextChanel1 = getString(R.string.channel_description_pin_reminder)
            val importanceChanel1 = NotificationManager.IMPORTANCE_DEFAULT
            val channelPinReminder =
                NotificationChannel(CHANEL_ID_PIN_REMINDER, nameChanel1, importanceChanel1).apply {
                    description = descriptionTextChanel1
                    setSound(null, null)
                }
            notificationManager.createNotificationChannel(channelPinReminder)

            val nameChanel2 = "TodoReminder"
            val descriptionTextChanel2 = "Description"
            val importanceChanel2 = NotificationManager.IMPORTANCE_HIGH
            val chanelTodoReminder =
                NotificationChannel(CHANEL_ID_TODO_REMINDER, nameChanel2, importanceChanel2).apply {
                    description = descriptionTextChanel2
                }
            notificationManager.createNotificationChannel(chanelTodoReminder)
        }
    }

}