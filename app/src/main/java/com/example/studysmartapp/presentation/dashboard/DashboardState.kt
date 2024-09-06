package com.example.studysmartapp.presentation.dashboard

import androidx.compose.ui.graphics.Color
import com.example.studysmartapp.domain.model.Session
import com.example.studysmartapp.domain.model.Subject

data class DashboardState(
    val totalSubjectCount:Int=0,
    val totalStudiedHours:Float=0f,
    val totalGoalStudyHours:Float=0f,
    val subjects:List<Subject> = emptyList(),
    var subjectName:String="",
    var goalStudyHours:String="",
    var subjectCardColors:List<Color> = Subject.subjectsCardColors.random(),
    val session: Session?=null
)