package com.example.studysmartapp.presentation.dashboard

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmartapp.domain.model.Session
import com.example.studysmartapp.domain.model.Subject
import com.example.studysmartapp.domain.model.Task
import com.example.studysmartapp.domain.repository.SessionRepository
import com.example.studysmartapp.domain.repository.SubjectRepository
import com.example.studysmartapp.domain.repository.TaskRepository
import com.example.studysmartapp.util.SnackbarEvent
import com.example.studysmartapp.util.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DaskboardViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository,
    private val taskRepository: TaskRepository
):ViewModel() {
    private val _state= MutableStateFlow(DashboardState())
    val state= combine(
        _state,
        subjectRepository.getTotalSubjectCount(),
        subjectRepository.getTotalGoalHours(),
        subjectRepository.getAllSubjects(),
        sessionRepository.getTotalSessionDuration()
    ){ _state,subjectCount,goalHours,subjects,totalSessionDuration ->
        _state.copy(
            totalSubjectCount=subjectCount,
            totalGoalStudyHours=goalHours,
            subjects=subjects,
            totalStudiedHours=totalSessionDuration.toHours()

        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardState()
    )

    val tasks:StateFlow<List<Task>> = taskRepository.getAllUpcomingTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val recentSession:StateFlow<List<Session>> = sessionRepository.getRecentFiveSessions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _snackbarEventFlow= MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow=_snackbarEventFlow.asSharedFlow()


    fun onEvent(event: DashboardEvent){
        when(event){
            DashboardEvent.DeleteSession ->{
                deleteSession()
            }

            DashboardEvent.SaveSubject ->saveSubject()

            is DashboardEvent.onDeleteSessionButtonClick ->{
                _state.update {
                    it.copy(session = event.session)
                }
            }
            is DashboardEvent.onGoalStudyHoursChange ->{
                _state.update {
                    it.copy(goalStudyHours = event.hours)
                }
            }
            is DashboardEvent.onSubjectCardColorChange -> {
                _state.update {
                    it.copy(subjectCardColors = event.colors)
                }
            }
            is DashboardEvent.onSubjectNameChange -> {
                _state.update {
                    it.copy(subjectName = event.name)
                }
            }
            is DashboardEvent.onTaskIsCompleteChange ->{

                    updateTask(event.task)

            }
        }
    }
    private fun deleteSession(){
        viewModelScope.launch {
            try {
                sessionRepository.deleteSession(state.value.session?:return@launch)
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar("Session deleted successfully")
                )
        }catch (e:Exception){
            _snackbarEventFlow.emit(
                SnackbarEvent.ShowSnackbar("Couldn't delete session ${e.message}",SnackbarDuration.Long)
            )

            }        }
    }

    private fun updateTask(task: Task){
        viewModelScope.launch {
            try {
                taskRepository.upsertTask(task = task.copy(
                    isComplete = !task.isComplete
                ))
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar("Saved in completed tasks.")
                )
            }catch (e:Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(message = "Couldn't update task ${e.message}",SnackbarDuration.Long)
                )
            }
        }
    }

    private fun saveSubject() {
        viewModelScope.launch {
            try {
                subjectRepository.upsertSubject(
                    subject = Subject(name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull()?:1f,
                        colors = state.value.subjectCardColors.map { it.toArgb() }
                    )
                )
                _state.update {
                    it.copy(
                        subjectName = "",
                        goalStudyHours = "",
                        subjectCardColors = Subject.subjectsCardColors.random()
                    )
                }
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar("Subject saved successfully")
                )
            }catch (e:Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar("Couldn't save subject. ${e.message}",
                    SnackbarDuration.Long)
                )


            }

        }
    }

}