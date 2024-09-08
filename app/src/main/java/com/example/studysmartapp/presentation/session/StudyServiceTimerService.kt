package com.example.studysmartapp.presentation.session

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.studysmartapp.util.Constants.ACTION_SERVICE_CANCEL
import com.example.studysmartapp.util.Constants.ACTION_SERVICE_START
import com.example.studysmartapp.util.Constants.ACTION_SERVICE_STOP
import com.example.studysmartapp.util.Constants.NOTIFICATION_CHANNEL_ID
import com.example.studysmartapp.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.studysmartapp.util.Constants.NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StudyServiceTimerService: Service() {
    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action.let {
            when(it){
                ACTION_SERVICE_START ->{

                }
                ACTION_SERVICE_STOP ->{

                }
                ACTION_SERVICE_CANCEL ->{

                }

            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService(){
        createNotificationChannel()
        startForeground(NOTIFICATION_ID,notificationBuilder.build())
    }
    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel=NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }


    }
}