package com.example.studysmartapp.presentation.task

import androidx.lifecycle.ViewModel
import com.example.studysmartapp.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaskViewModel@Inject constructor(
    private val taskRepository: TaskRepository
):ViewModel() {
}