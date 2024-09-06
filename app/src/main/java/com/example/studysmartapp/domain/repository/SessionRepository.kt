package com.example.studysmartapp.domain.repository


import com.example.studysmartapp.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    suspend fun insertSession(session: Session)

    suspend fun deleteSession(session: Session)

    fun getAllSessions(): Flow<List<Session>>

    fun getRecentSessionsForSubject(subjectId:Int): Flow<List<Session>>

    fun getTotalSessionDuration(): Flow<Long>

    fun getTotalSessionDurationBySubjectId(subjectId: Int): Flow<Long>

    fun deleteSessionsBySubjectId(subjectId:Int)
}