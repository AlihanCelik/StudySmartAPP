package com.example.studysmartapp.presentation.task

import com.example.studysmartapp.domain.model.Subject
import com.example.studysmartapp.util.Priority

data class TaskState (
    val title:String="",
    val description:String="",
    val dueDate:Long?=null,
    val isTaskComplete:Boolean=false,
    val priority: Priority=Priority.LOW,
    val relatedToSubject:String?=null,
    val subjects:List<Subject> = emptyList(),
    val subjectId:Int?=null,
    val currentTaskId:Int?=null
)