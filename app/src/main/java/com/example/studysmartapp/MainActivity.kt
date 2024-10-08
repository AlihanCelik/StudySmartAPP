package com.example.studysmartapp

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import com.example.studysmartapp.domain.model.Session
import com.example.studysmartapp.domain.model.Subject
import com.example.studysmartapp.domain.model.Task
import com.example.studysmartapp.presentation.NavGraphs
import com.example.studysmartapp.presentation.destinations.SessionScreenRouteDestination
import com.example.studysmartapp.presentation.session.StudyServiceTimerService
import com.example.studysmartapp.ui.theme.StudySmartAPPTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var isBound by mutableStateOf(false)
    private lateinit var timerService: StudyServiceTimerService
    private val connection = object : ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder= p1 as StudyServiceTimerService.StudySessionTimerBinder
            timerService=binder.getService()
            isBound=true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
        }

    }

    override fun onStart() {
        super.onStart()
        Intent(this,StudyServiceTimerService::class.java).also { intent->
            bindService(intent,connection,Context.BIND_AUTO_CREATE)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if(isBound){
                StudySmartAPPTheme {
                    DestinationsNavHost(navGraph = NavGraphs.root,
                        dependenciesContainerBuilder = {
                            dependency(SessionScreenRouteDestination){timerService}
                        })

                }
            }

        }
        requestPermission()
    }
    private fun requestPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),0
            )
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isBound=false
    }
}
val subjects= listOf(
    Subject("English", goalHours = 10f, colors = Subject.subjectsCardColors[0].map { it.toArgb() }, subjectId = 0),
    Subject("Maths", goalHours = 10f, colors = Subject.subjectsCardColors[1].map { it.toArgb() }, subjectId = 0),
    Subject("physics", goalHours = 10f, colors = Subject.subjectsCardColors[2].map { it.toArgb() }, subjectId = 0),
    Subject("Geology", goalHours = 10f, colors = Subject.subjectsCardColors[3].map { it.toArgb() }, subjectId = 0),
    Subject("Fine Arts", goalHours = 10f, colors = Subject.subjectsCardColors[4].map { it.toArgb() }, subjectId = 0),
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
