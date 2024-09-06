package com.example.studysmartapp.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmartapp.domain.repository.SessionRepository
import com.example.studysmartapp.domain.repository.SubjectRepository
import com.example.studysmartapp.util.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DaskboardViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository
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

    fun onEvent(event: DashboardEvent){
        when(event){
            DashboardEvent.DeleteSubject -> TODO()
            DashboardEvent.SaveSubject -> TODO()
            is DashboardEvent.onDeleteSessionButtonClick -> TODO()
            is DashboardEvent.onGoalStudyHoursChange -> TODO()
            is DashboardEvent.onSubjectCardColorChange -> TODO()
            is DashboardEvent.onSubjectNameChange -> TODO()
            is DashboardEvent.onTaskIsCompleteChange -> TODO()
        }
    }
}