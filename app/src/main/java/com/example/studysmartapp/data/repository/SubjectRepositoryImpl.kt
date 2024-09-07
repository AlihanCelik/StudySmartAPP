package com.example.studysmartapp.data.repository

import com.example.studysmartapp.data.local.SessionDao
import com.example.studysmartapp.data.local.SubjectDao
import com.example.studysmartapp.data.local.TaskDao
import com.example.studysmartapp.domain.model.Subject
import com.example.studysmartapp.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao,
    private val sessionDao:SessionDao,
    private val taskDao:TaskDao
):SubjectRepository {
    override suspend fun upsertSubject(subject: Subject) {
        subjectDao.upsertSubject(subject)
    }

    override fun getTotalSubjectCount(): Flow<Int> {
        return subjectDao.getTotalSubjectCount()
    }

    override fun getTotalGoalHours(): Flow<Float> {
        return subjectDao.getTotalGoalHours()
    }

    override suspend fun getSubjectById(subjectId: Int): Subject? {
        return subjectDao.getSubjectById(subjectId)
    }

    override suspend fun deleteSubject(subjectId: Int) {
        subjectDao.deleteSubject(subjectId)
        sessionDao.deleteSessionsBySubjectId(subjectId)
        taskDao.deleteTasksBySubjectId(subjectId)
    }

    override fun getAllSubjects(): Flow<List<Subject>> {
        return  subjectDao.getAllSubjects()
    }
}