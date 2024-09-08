package com.example.studysmartapp.presentation.task

import com.example.studysmartapp.domain.model.Subject
import com.example.studysmartapp.util.Priority


sealed class TaskEvent {
    data class onTitleChange(val title:String):TaskEvent()
    data class onDescriptionChange(val description: String):TaskEvent()
    data class onDateChange(val mills:Long?):TaskEvent()
    data class onPriorityChange(val priority: Priority):TaskEvent()
    data class onRelatedSubjectSelect(val subject: Subject):TaskEvent()
    data object onIsCompleteChange:TaskEvent()
    data object DeleteTask:TaskEvent()
    data object SaveTask:TaskEvent()

}