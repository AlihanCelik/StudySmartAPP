package com.example.studysmartapp.presentation.task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmartapp.domain.model.Task
import com.example.studysmartapp.domain.repository.SubjectRepository
import com.example.studysmartapp.domain.repository.TaskRepository
import com.example.studysmartapp.presentation.navArgs
import com.example.studysmartapp.presentation.subject.SubjectScreenNavArgs
import com.example.studysmartapp.subjects
import com.example.studysmartapp.util.Priority
import com.example.studysmartapp.util.SnackbarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class TaskViewModel@Inject constructor(
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    savedStateHandle: SavedStateHandle
):ViewModel() {
    private val navArgs:TaskScreenNavArgs=savedStateHandle.navArgs()
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
    private val _snackbarEventFlow= MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow=_snackbarEventFlow.asSharedFlow()

    init {
        fetchTask()
        fetchSubject()
    }
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
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar("Please select subject to the task"))
                return@launch
            }
            try {
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
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar("Task Saved Successfully"))
            }catch (e:Exception){
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar("Couldn't save task ${e.message}"))
            }

        }
    }
    private fun deleteTask(){
        viewModelScope.launch {
            try {

            }catch (e:Exception){

            }
        }
    }

    private fun fetchTask(){
        viewModelScope.launch {
            navArgs.taskId?.let {id->
                taskRepository.getTaskById(id)?.let {task->
                    _state.update {
                        it.copy(
                            title = task.title,
                            description = task.description,
                            dueDate = task.dueDate,
                            priority = Priority.fromInt(task.priority),
                            relatedToSubject = task.relatedToSubject,
                            isTaskComplete = task.isComplete,
                            currentTaskId = task.taskSubjectId,
                            subjectId = task.taskSubjectId
                        )
                    }

                }
            }
        }
    }
    private fun fetchSubject(){
        viewModelScope.launch {
            navArgs.subjectId?.let { id->
                subjectRepository.getSubjectById(id)?.let { subject ->
                    _state.update {
                        it.copy(
                            subjectId = subject.subjectId,
                            relatedToSubject = subject.name
                        )
                    }
                }
            }
        }
    }



}