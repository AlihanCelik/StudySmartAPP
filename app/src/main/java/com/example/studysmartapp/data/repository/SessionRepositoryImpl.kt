package com.example.studysmartapp.data.repository

import com.example.studysmartapp.data.local.SessionDao
import com.example.studysmartapp.domain.model.Session
import com.example.studysmartapp.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao
) :SessionRepository {
    override suspend fun insertSession(session: Session) {
        sessionDao.insertSession(session)
    }

    override suspend fun deleteSession(session: Session) {
        sessionDao.deleteSession(session)
    }

    override fun getAllSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions()
    }

    override fun getRecentSessionsForSubject(subjectId: Int): Flow<List<Session>> {
       return sessionDao.getRecentSessionsForSubject(subjectId)
    }

    override fun getTotalSessionDuration(): Flow<Long> {
        return sessionDao.getTotalSessionDuration()
    }

    override fun getTotalSessionDurationBySubjectId(subjectId: Int): Flow<Long> {
        return sessionDao.getTotalSessionDurationBySubjectId(subjectId)
    }

    override fun deleteSessionsBySubjectId(subjectId: Int) {
         sessionDao.deleteSessionsBySubjectId(subjectId)
    }
}