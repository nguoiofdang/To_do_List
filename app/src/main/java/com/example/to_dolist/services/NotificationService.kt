package com.example.to_dolist.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.to_dolist.R
import com.example.to_dolist.ui.MainActivity
import com.example.to_dolist.utils.Constance.CHANEL_ID_PIN_REMINDER

class NotificationService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val notification = NotificationCompat.Builder(this, CHANEL_ID_PIN_REMINDER)
            .setContentTitle("Thêm nhiệm vụ")
            .setContentText("Tận hưởng ngày của bạn")
            .setSmallIcon(R.drawable.ic_app)
            .setContentIntent(pendingIntent)
            .setSound(null)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setOngoing(true)
            .setSilent(true)
        startForeground(-1, notification.build())

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("service", "Destroy")
    }
}