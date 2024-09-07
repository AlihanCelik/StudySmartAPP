package com.example.studysmartapp.presentation.subject

import androidx.compose.ui.graphics.Color
import com.example.studysmartapp.domain.model.Session
import com.example.studysmartapp.domain.model.Subject
import com.example.studysmartapp.domain.model.Task

sealed class SubjectEvent {

    data object UpdateSubject:SubjectEvent()
    data object DeleteSubject:SubjectEvent()
    data object DeleteSessipn:SubjectEvent()
    data object UpdateProgress:SubjectEvent()
    data class onTaskIsComplteChange(val task:Task):SubjectEvent()
    data class onSubjectCardColorChange(val color:List<Color>):SubjectEvent()
    data class onSubjectNameChange(val name :String):SubjectEvent()
    data class onGoalStudyHoursChange(val hours:String):SubjectEvent()
    data class onDeleteSessionButtonClick(val session: Session):SubjectEvent()
}