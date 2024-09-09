package com.example.studysmartapp.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmartapp.domain.model.Session
import com.example.studysmartapp.domain.repository.SessionRepository
import com.example.studysmartapp.domain.repository.SubjectRepository
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
class SessionViewModel@Inject constructor(
    private val sessionRepository: SessionRepository,
    private val subjectRepository: SubjectRepository
):ViewModel() {
    private val _state = MutableStateFlow(SessionState())
    val state = combine(
        _state,
        subjectRepository.getAllSubjects(),
        sessionRepository.getAllSessions()
    ) { state, subjects, sessions ->
        state.copy(
            subjects = subjects,
            sessions = sessions
        )


    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SessionState()

    )
    private val _snackbarEventFlow= MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow=_snackbarEventFlow.asSharedFlow()

    fun onEvent(event: SessionEvent) {
        when (event) {
            is SessionEvent.onDeleteSessionButtonClick -> {
                _state.update {
                    it.copy(session = event.session)
                }

            }
            SessionEvent.DeleteSession -> DeleteSession()
            is SessionEvent.SaveSession -> insertSession(event.duration)
            is SessionEvent.UpdateSubjectIdAndRelatedSubject -> {
                _state.update {
                    it.copy(subjectId = event.subjectId,
                        relatedToSubject = event.relatedToSubject)
                }
            }
            is SessionEvent.onRelatedSubjectChange ->{
                _state.update {
                    it.copy(relatedToSubject = event.subject.name,
                        subjectId = event.subject.subjectId)
                }
            }

            SessionEvent.NotifyToUpdateSubject -> {
                notifyToUpdateSubject()
            }
        }
    }
    private fun notifyToUpdateSubject(){
        viewModelScope.launch {
            if (state.value.subjectId == null || state.value.relatedToSubject == null) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Please select subject related to the session."
                    )
                )
            }
        }
    }

    private fun DeleteSession(){
        viewModelScope.launch {
            try {
                sessionRepository.deleteSession(state.value.session?:return@launch)
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar("Session deleted successfully" ))

        }catch (e:Exception){
            _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar("Couldn't delete session ${e.message}" ))

            }        }
    }

    private fun insertSession(duration: Long){
        viewModelScope.launch {
            if (duration < 36) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        message = "Single session can not be less than 36 seconds"
                    )
                )
                return@launch
            }
            try {
                sessionRepository.insertSession(
                    session = Session(
                        sessionSubjctId = state.value.subjectId?:-1,
                        relatedToSubject = state.value.relatedToSubject ?: "",
                        date = Instant.now().toEpochMilli(),
                        duration=duration
                    )

                )
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar("Session saved successfully" ))
            }catch (e:Exception){
                _snackbarEventFlow.emit(SnackbarEvent.ShowSnackbar("Couldn't save session ${e.message}" ))

            }

        }
    }
}

