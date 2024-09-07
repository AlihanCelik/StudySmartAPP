package com.example.studysmartapp.presentation.subject

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmartapp.domain.repository.SessionRepository
import com.example.studysmartapp.domain.repository.SubjectRepository
import com.example.studysmartapp.domain.repository.TaskRepository
import com.example.studysmartapp.presentation.navArgs
import com.example.studysmartapp.util.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val taskRepository: TaskRepository,
    private val sessionRepository: SessionRepository,
    savedStateHandle: SavedStateHandle
):ViewModel() {
    private val navArgs:SubjectScreenNavArgs=savedStateHandle.navArgs()

    private val _state= MutableStateFlow(SubjectState())
    val state= combine(
        _state,
        taskRepository.getAllUpcomingTasks(),
        taskRepository.getCompletedTasksForSubject(navArgs.subjectId),
        sessionRepository.getRecentTenSessionsForSubject(navArgs.subjectId),
        sessionRepository.getTotalSessionDurationBySubjectId(navArgs.subjectId)
    ){state ,upcoming,completedTask,recentSession,totalSessionsDuration->
        state.copy(
            upcomingTasks = upcoming,
            completedTasks = completedTask,
            recentSessions = recentSession,
            studiedHours = totalSessionsDuration.toHours()
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SubjectState()
    )

    fun onEvent(event: SubjectEvent){
        when(event){
            SubjectEvent.DeleteSessipn -> TODO()
            SubjectEvent.DeleteSubject -> TODO()
            SubjectEvent.UpdateSubject -> TODO()
            is SubjectEvent.onDeleteSessionButtonClick -> TODO()
            is SubjectEvent.onGoalStudyHoursChange -> TODO()
            is SubjectEvent.onSubjectCardColorChange -> TODO()
            is SubjectEvent.onSubjectNameChange -> TODO()
            is SubjectEvent.onTaskIsComplteChange -> TODO()
        }
    }
}