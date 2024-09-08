package com.example.studysmartapp.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmartapp.domain.model.Task
import com.example.studysmartapp.domain.repository.SubjectRepository
import com.example.studysmartapp.domain.repository.TaskRepository
import com.example.studysmartapp.presentation.navArgs
import com.example.studysmartapp.presentation.subject.SubjectScreenNavArgs
import com.example.studysmartapp.subjects
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class TaskViewModel@Inject constructor(
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository
):ViewModel() {
    private val _state= MutableStateFlow(TaskState())
    val state= combine(
        _state,
        subjectRepository.getAllSubjects()

    ){state,subjects ->
        state.copy(subjects = subjects)

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskState()
    )
    fun onEvent(event: TaskEvent){
        when(event){
            TaskEvent.DeleteTask -> TODO()
            TaskEvent.SaveTask -> saveTask()
            is TaskEvent.onDateChange ->{
                _state.update {
                    it.copy(dueDate = event.mills)
                }
            }
            is TaskEvent.onDescriptionChange ->{
                _state.update {
                    it.copy(description = event.description)
                }
            }
            TaskEvent.onIsCompleteChange -> TODO()
            is TaskEvent.onPriorityChange ->{
                _state.update {
                    it.copy(priority = event.priority)
                }
            }
            is TaskEvent.onRelatedSubjectSelect ->{
                _state.update {
                    it.copy(relatedToSubject = event.subject.name,
                        subjectId = event.subject.subjectId)
                }
            }
            is TaskEvent.onTitleChange -> {
                _state.update {
                    it.copy(title = event.title)
                }
            }
        }
    }
    private fun saveTask(){
        viewModelScope.launch {
            val state=_state.value
            if(state.subjectId==null || state.relatedToSubject==null){
                return@launch
            }
            taskRepository.upsertTask(
                task = Task(
                    title = state.title,
                    description = state.description,
                    dueDate = state.dueDate?:Instant.now().toEpochMilli(),
                    relatedToSubject = state.relatedToSubject,
                    priority = state.priority.value,
                    isComplete = state.isTaskComplete,
                    taskSubjectId = state.subjectId,
                    taskId = state.currentTaskId
                )
            )
        }
    }
    private fun deleteTask(){
        viewModelScope.launch {
            try {

            }catch (e:Exception){

            }
        }
    }



}