package com.example.studysmartapp.presentation.dashboard

import androidx.lifecycle.ViewModel
import com.example.studysmartapp.domain.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DaskboardViewModel@Inject constructor(
    private val subjectRepository: SubjectRepository
):ViewModel() {
}