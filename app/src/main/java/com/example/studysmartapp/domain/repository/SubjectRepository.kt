package com.example.studysmartapp.domain.repository

import com.example.studysmartapp.domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {

    suspend fun upsertSubject(subject: Subject)

    fun getTotalSubjectCount():Flow<Int>

    fun getTotalGoalHours(): Flow<Float>

    suspend fun getSubjectById(subjectId:Int): Subject?

    suspend fun deleteSubject(subjectId:Int)

    fun getAllSubjects(): Flow<List<Subject>>
}