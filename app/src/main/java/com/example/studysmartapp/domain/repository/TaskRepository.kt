package com.example.studysmartapp.domain.repository

import com.example.studysmartapp.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun upsertTask(task: Task)

    suspend fun deleteTask(taskId:Int)

    suspend fun deleteTasksBySubjectId(subjetId:Int)

    suspend fun getTaskById(taskId: Int): Task?

    fun getTasksForSubject(subjectId:Int): Flow<List<Task>>

    fun getAllTasks(): Flow<List<Task>>
}