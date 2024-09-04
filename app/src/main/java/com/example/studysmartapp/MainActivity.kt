package com.example.studysmartapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.studysmartapp.domain.model.Session
import com.example.studysmartapp.domain.model.Subject
import com.example.studysmartapp.domain.model.Task
import com.example.studysmartapp.presentation.dashboard.DashBoardScreen
import com.example.studysmartapp.presentation.subject.SubjectScreen
import com.example.studysmartapp.ui.theme.StudySmartAPPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudySmartAPPTheme {
                SubjectScreen()
            }
        }
    }
}
val subjects= listOf(
    Subject("English", goalHours = 10f, colors = Subject.subjectsCardColors[0], subjectId = 0),
    Subject("Maths", goalHours = 10f, colors = Subject.subjectsCardColors[1], subjectId = 0),
    Subject("physics", goalHours = 10f, colors = Subject.subjectsCardColors[2], subjectId = 0),
    Subject("Geology", goalHours = 10f, colors = Subject.subjectsCardColors[3], subjectId = 0),
    Subject("Fine Arts", goalHours = 10f, colors = Subject.subjectsCardColors[4], subjectId = 0),
)
val tasks= listOf(
    Task(
        title="Prepare notes",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        isComplete = false,
        taskSubjectId = 0,
        taskId = 1),
    Task(
        title="Do Homework",
        description = "",
        dueDate = 0L,
        priority = 2,
        relatedToSubject = "",
        isComplete = true,
        taskSubjectId = 0,
        taskId = 1),
    Task(
        title="Go Coaching",
        description = "",
        dueDate = 0L,
        priority = 0,
        relatedToSubject = "",
        isComplete = false,
        taskSubjectId = 0,
        taskId = 1),
    Task(
        title="Assignment",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        isComplete = false,
        taskSubjectId = 0,
        taskId = 1),
    Task(
        title="Write Poem",
        description = "",
        dueDate = 0L,
        priority = 0,
        relatedToSubject = "",
        isComplete = true,
        taskSubjectId = 0,
        taskId = 1)
)
val sessions= listOf(
    Session(
        relatedToSubject = "English",
        date = 0L,
        duration = 2,
        sessionSubjctId = 0,
        sessionId = 0
    ),
    Session(
        relatedToSubject = "Maths",
        date = 0L,
        duration = 2,
        sessionSubjctId = 0,
        sessionId = 0
    ),
    Session(
        relatedToSubject = "physics",
        date = 0L,
        duration = 2,
        sessionSubjctId = 0,
        sessionId = 0
    ),
    Session(
        relatedToSubject = "Chemistry",
        date = 0L,
        duration = 2,
        sessionSubjctId = 0,
        sessionId = 0
    )
)
