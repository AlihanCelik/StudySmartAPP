package com.example.studysmartapp.presentation.subject

import androidx.lifecycle.ViewModel
import com.example.studysmartapp.domain.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel@Inject constructor(
    private val subjectRepository: SubjectRepository
):ViewModel() {

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