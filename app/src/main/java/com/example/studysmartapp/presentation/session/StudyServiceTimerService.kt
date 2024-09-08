package com.example.studysmartapp.presentation.session

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.example.studysmartapp.util.Constants.ACTION_SERVICE_CANCEL
import com.example.studysmartapp.util.Constants.ACTION_SERVICE_START
import com.example.studysmartapp.util.Constants.ACTION_SERVICE_STOP
import com.example.studysmartapp.util.Constants.NOTIFICATION_CHANNEL_ID
import com.example.studysmartapp.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.studysmartapp.util.Constants.NOTIFICATION_ID
import com.example.studysmartapp.util.pad
import com.example.studysmartapp.util.toHours
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class StudyServiceTimerService: Service() {
    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    private lateinit var timer:Timer

    var duration:Duration=Duration.ZERO
        private set

    var seconds= mutableStateOf("00")
        private set

    var minutes= mutableStateOf("00")
        private set

    var hours= mutableStateOf("00")
        private set

    var currentTimerState= mutableStateOf(TimerState.IDLE)
        private set

    override fun onBind(p0: Intent?): IBinder? =null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action.let {
            when(it){
                ACTION_SERVICE_START ->{
                    startForegroundService()
                    startTime{hours,minutes,seconds ->
                        updateNotification(hours,minutes,seconds)
                    }
                }
                ACTION_SERVICE_STOP ->{
                    stopTimer()
                }
                ACTION_SERVICE_CANCEL ->{
                    stopTimer()
                    cancelTimer()
                    stopForegroundService()

                }

            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    private fun stopForegroundService(){
        notificationManager.cancel(NOTIFICATION_ID)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
        stopSelf()
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
    private fun updateNotification(hours:String,minutes:String,seconds:String){
        notificationManager.notify(
            NOTIFICATION_ID,notificationBuilder.setContentText("$hours:$minutes:$seconds").build()
        )
    }
    private fun stopTimer(){
        if(this::timer.isInitialized){
            timer.cancel()

        }
        currentTimerState.value=TimerState.STOPPED
    }

    private fun cancelTimer(){
        duration=Duration.ZERO
        updateTimeUnits()
        currentTimerState.value=TimerState.IDLE
    }


    private fun startTime(onTick:(h:String,m:String,s:String)->Unit
    ){
        currentTimerState.value=TimerState.STARTED
        timer= fixedRateTimer(initialDelay = 1000L, period = 1000L){
            duration=duration.plus(1.seconds)
            updateTimeUnits()
            onTick(hours.value,minutes.value,seconds.value)
        }
    }
    private fun updateTimeUnits(){
        duration.toComponents{hours,minutes,seconds, _ ->
            this@StudyServiceTimerService.hours.value=hours.toInt().pad()
            this@StudyServiceTimerService.minutes.value=minutes.pad()
            this@StudyServiceTimerService.seconds.value=seconds.pad()

        }
    }
}

enum class TimerState{
    IDLE,
    STARTED,
    STOPPED
}