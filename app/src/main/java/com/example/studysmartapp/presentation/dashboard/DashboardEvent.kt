package com.example.studysmartapp.presentation.dashboard

import androidx.compose.ui.graphics.Color
import com.example.studysmartapp.domain.model.Session
import com.example.studysmartapp.domain.model.Task

sealed class DashboardEvent {
    data object SaveSubject:DashboardEvent()
    data object DeleteSubject:DashboardEvent()
    data class onDeleteSessionButtonClick(val session: Session) : DashboardEvent()
    data class onTaskIsCompleteChange(val task: Task):DashboardEvent()
    data class onSubjectCardColorChange(val colors:List<Color>):DashboardEvent()
    data class onSubjectNameChange(val name:String):DashboardEvent()
    data class onGoalStudyHoursChange(val hours:String):DashboardEvent()
}