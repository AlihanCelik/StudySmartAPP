package com.example.studysmartapp.domain.model

import kotlin.time.Duration

data class Session(
    val sessionSubjctId: Int,
    val relatedToSubject: String,
    val date: Long,
    val duration: Long,
    val sessionId:Int
)
