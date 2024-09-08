package com.example.studysmartapp.presentation.subject

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmartapp.domain.model.Subject
import com.example.studysmartapp.domain.model.Task
import com.example.studysmartapp.domain.repository.SessionRepository
import com.example.studysmartapp.domain.repository.SubjectRepository
import com.example.studysmartapp.domain.repository.TaskRepository
import com.example.studysmartapp.presentation.navArgs
import com.example.studysmartapp.util.SnackbarEvent
import com.example.studysmartapp.util.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private val _snackbarEventFlow= MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow=_snackbarEventFlow.asSharedFlow()

    init {
        fetchSubject()
    }

    fun onEvent(event: SubjectEvent){
        when(event){
            SubjectEvent.DeleteSession -> deleteSession()
            SubjectEvent.DeleteSubject -> deleteSubject()
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
                updateTask(event.task)

            }

            SubjectEvent.UpdateProgress -> {
                val goalStudyHours= state.value.goalStudyHours.toFloatOrNull()?:1f
                _state.update {
                    it.copy(
                        progress = (state.value.studiedHours/goalStudyHours).coerceIn(0f,1f)
                    )
                }
            }
        }
    }

    private fun deleteSession(){
        viewModelScope.launch {
            try {
                state.value.session?.let {
                    sessionRepository.deleteSession(it)
                }
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar("Session deleted successfully.")
                )



            }catch (e:Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar("Couldn't delete session ${e.message}",SnackbarDuration.Long)
                )

            }
        }
    }
    private fun deleteSubject(){
        viewModelScope.launch {
            try {
                val currentSubjectId=state.value.currentSubjectId
                if(currentSubjectId!=null){
                    withContext(Dispatchers.IO){

                        subjectRepository.deleteSubject(subjectId = currentSubjectId)
                    }
                    _snackbarEventFlow.emit(
                            SnackbarEvent.ShowSnackbar(message = "Subject deleted successfully")
                    )
                    _snackbarEventFlow.emit(SnackbarEvent.NavigateUp)
                }else{
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "No Subject to delete")
                    )
                }
            }catch (e:Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(message = "Couldn't delete subject ${e.message}",SnackbarDuration.Long)
                )

            }
        }
    }
    private fun updateTask(task: Task){
        viewModelScope.launch {
            try {
                taskRepository.upsertTask(task = task.copy(
                    isComplete = !task.isComplete
                ))
                if (task.isComplete){
                    _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar(message = "Saved in upcoming tasks"))
                }else{
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar("Saved in completed tasks.")
                    )
                }

            }catch (e:Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(message = "Couldn't update task ${e.message}",SnackbarDuration.Long)
                )
            }
        }
    }
    private fun updateSubject(){
            viewModelScope.launch {
                try {
                subjectRepository.upsertSubject(subject = Subject(
                    subjectId = state.value.currentSubjectId,
                    name = state.value.subjectName,
                    goalHours = state.value.goalStudyHours.toFloatOrNull()?:1f,
                    colors = state.value.subjectCardColors.map {  it.toArgb()}
                ))
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar("Subject updated successfully.")
                )
            }catch (e:Exception){
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "Couldn't update subject. ${e.message}",SnackbarDuration.Long)
                    )
        }
        }

    }
    private fun fetchSubject(){
        viewModelScope.launch {
            subjectRepository.getSubjectById(navArgs.subjectId)?.let { subject->
                _state.update {
                    it.copy(
                        currentSubjectId = subject.subjectId,
                        subjectName = subject.name,
                        goalStudyHours = subject.goalHours.toString(),
                        subjectCardColors = subject.colors.map { Color(it) }

                    )
                }

            }
        }
    }
}