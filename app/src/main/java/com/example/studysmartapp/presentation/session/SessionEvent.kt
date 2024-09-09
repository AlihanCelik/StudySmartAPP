package com.example.studysmartapp.presentation.session

import com.example.studysmartapp.domain.model.Session
import com.example.studysmartapp.domain.model.Subject

sealed class SessionEvent {

    data class onRelatedSubjectChange(val subject:Subject):SessionEvent()

    data class SaveSession(val duration: Long):SessionEvent()

    data class onDeleteSessionButtonClick(val session: Session):SessionEvent()

    data object DeleteSession:SessionEvent()

    data object CheckSubjectId:SessionEvent()

    data class  UpdateSubjectIdAndRelatedSubject(val subjectId:Int,val relatedToSubject:String):SessionEvent()
}