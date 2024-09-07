package com.example.studysmartapp.presentation.subject

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmartapp.domain.model.Subject
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    init {
        fetchSubject()
    }

    fun onEvent(event: SubjectEvent){
        when(event){
            SubjectEvent.DeleteSessipn -> TODO()
            SubjectEvent.DeleteSubject -> TODO()
            SubjectEvent.UpdateSubject -> updateSubject()
            is SubjectEvent.onDeleteSessionButtonClick ->{
                _state.update {
                    it.copy(session = event.session)
                }
            }
            is SubjectEvent.onGoalStudyHoursChange ->{
                _state.update {
                    it.copy(goalStudyHours = event.hours)
                }
            }
            is SubjectEvent.onSubjectCardColorChange ->{
                _state.update {
                    it.copy(subjectCardColors = event.color)
                }
            }
            is SubjectEvent.onSubjectNameChange ->{
                _state.update {
                    it.copy(subjectName = event.name)
                }
            }
            is SubjectEvent.onTaskIsComplteChange ->{

            }
        }
    }
    private fun updateSubject(){
        viewModelScope.launch {
            subjectRepository.upsertSubject(subject = Subject(
                subjectId = state.value.currentSubjectId,
                name = state.value.subjectName,
                goalHours = state.value.goalStudyHours.toFloatOrNull()?:1f,
                colors = state.value.subjectCardColors.map {  it.toArgb()}
            ))
        }
    }
    private fun fetchSubject(){
        viewModelScope.launch {
            subjectRepository.getSubjectById(navArgs.subjectId)?.let { subject->
                _state.update {
                    it.copy(
                        subjectName = subject.name,
                        goalStudyHours = subject.goalHours.toString(),
                        subjectCardColors = subject.colors.map { Color(it) }

                    )
                }

            }
        }
    }
}