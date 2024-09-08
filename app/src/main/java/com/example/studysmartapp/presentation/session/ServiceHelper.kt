package com.example.studysmartapp.presentation.session

import android.content.Context
import android.content.Intent

object ServiceHelper {
    fun triggerForegroundService(context: Context,action:String){
        Intent(context,StudyServiceTimerService::class.java).apply {
            this.action=action
            context.startService(this)
        }
    }
}