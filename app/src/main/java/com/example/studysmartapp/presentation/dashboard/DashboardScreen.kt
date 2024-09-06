package com.example.studysmartapp.presentation.dashboard

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHost
import androidx.room.RoomOpenHelper
import com.example.studysmartapp.R
import com.example.studysmartapp.domain.model.Session
import com.example.studysmartapp.domain.model.Subject
import com.example.studysmartapp.domain.model.Task
import com.example.studysmartapp.presentation.components.AddSubjectDialog
import com.example.studysmartapp.presentation.components.CountCard
import com.example.studysmartapp.presentation.components.DeleteDiaLog
import com.example.studysmartapp.presentation.components.SubjectCard
import com.example.studysmartapp.presentation.components.TaskCheckBox
import com.example.studysmartapp.presentation.components.studySessionsList
import com.example.studysmartapp.presentation.components.tasksList
import com.example.studysmartapp.presentation.destinations.SessionScreenRouteDestination
import com.example.studysmartapp.presentation.destinations.SubjectScreenRouteDestination
import com.example.studysmartapp.presentation.destinations.TaskScreenRouteDestination
import com.example.studysmartapp.presentation.subject.SubjectScreenNavArgs
import com.example.studysmartapp.presentation.task.TaskScreenNavArgs
import com.example.studysmartapp.sessions
import com.example.studysmartapp.subjects
import com.example.studysmartapp.tasks
import com.example.studysmartapp.ui.theme.StudySmartAPPTheme
import com.example.studysmartapp.util.SnackbarEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import java.time.ZoneId


@Destination(start = true)
@Composable
fun DashboardScreenRoute(
    navigator:DestinationsNavigator
){
    val viewModel:DaskboardViewModel= hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    DashBoardScreen(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarEvent = viewModel.snackbarEventFlow,
        onSubjectCardClick ={subjectId->
            subjectId?.let {
                val navArg=SubjectScreenNavArgs(subjectId=subjectId)
                navigator.navigate(SubjectScreenRouteDestination(navArgs = navArg))
            }
        } ,
        onTaskCardClick = {taskId->
            val navArg=TaskScreenNavArgs(taskId=taskId, subjectId = null)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))

        },
        onStartSessionButtonClick = {
            navigator.navigate(SessionScreenRouteDestination())
        }
        )
}

@Composable
private fun DashBoardScreen(
    state: DashboardState,
    onEvent:(DashboardEvent)->Unit,
    snackbarEvent:SharedFlow<SnackbarEvent>,
    onSubjectCardClick:(Int?)->Unit,
    onTaskCardClick:(Int?)->Unit,
    onStartSessionButtonClick:()->Unit

){

    var isAddSubjectDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var isDeleteDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val snackbarHostState=remember{SnackbarHostState()}

    LaunchedEffect(key1 = true) {
        snackbarEvent.collectLatest { event->
            when(event){
                is SnackbarEvent.ShowSnackbar->{
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }
            }
        }

    }
    
    AddSubjectDialog(
        isOpen = isAddSubjectDialog,
        goalHours=state.goalStudyHours,
        subjectName = state.subjectName,
        selectedColors = state.subjectCardColors,
        onSubjectNameChange = {onEvent(DashboardEvent.onSubjectNameChange(it))},
        onGoalHoursChange = {onEvent(DashboardEvent.onGoalStudyHoursChange(it))},
        onColorChange = {onEvent(DashboardEvent.onSubjectCardColorChange(it))},
        onDismissRequest = { isAddSubjectDialog=false },
        onConfirmButtonClick = {
            onEvent(DashboardEvent.SaveSubject)
            isAddSubjectDialog=false
        })
    DeleteDiaLog(
        isOpen = isDeleteDialog,
        title = "Delete Session1",
        onBodyText = "Are you sure, you want to dlete this session? Your studied hours will be reduced" +
                "by this session time. This action can not be unde",
        onDismissRequest = {isDeleteDialog=false},
        onConfirmButtonClick = {
            onEvent(DashboardEvent.DeleteSession)
            isDeleteDialog=false}
    )
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState)},
        topBar = { DashboardScreenTopBar() }
    ) {paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item{
                CountCardsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    subjectCount = state.totalSubjectCount,
                    studiedHours = state.totalStudiedHours.toString(),
                    goalHours = state.totalGoalStudyHours.toString()
                )
            }
            item {
                SubjectsCardsSection(
                    modifier = Modifier.fillMaxWidth(),
                    subjectList = state.subjects,
                    onAddClicked = {isAddSubjectDialog=true},
                    onSubjectCardClick = onSubjectCardClick)
            }
            item { 
                Button(onClick = onStartSessionButtonClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 20.dp)
                ) {
                    Text(text = "Start Study Session")
                    
                }
            }
            tasksList(
                sectionTitle = "UPCOMING TASKS",
                emptyListText = "You don't have any upcoming tasks.\n" +
                        "Click the + button in subject screen to add new task.",
                tasks =tasks,
                onCheckBoxClick = {onEvent(DashboardEvent.onTaskIsCompleteChange(it))},
                onTaskCardClick = onTaskCardClick
            )
            studySessionsList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = "You don't have any upcoming tasks. \n" +
                        "Click the + button in subject screen to add new task.",
                sessions = sessions,
                onDeleteClick = {
                    onEvent(DashboardEvent.onDeleteSessionButtonClick(it))
                    isDeleteDialog=true}
            )


        }


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScreenTopBar(){
    CenterAlignedTopAppBar(title = {
        Text(
            text = "StudySmart",
            style = MaterialTheme.typography.displaySmall)
    })
}

@Composable
private fun CountCardsSection(
    modifier: Modifier,
    subjectCount:Int,
    studiedHours:String,
    goalHours:String
){
    Row (modifier=modifier){
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Subject Count",
            count = subjectCount.toString()
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Studied Hours",
            count = studiedHours
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Goal Study Hours",
            count = goalHours
        )
    }
}

@Composable
private fun SubjectsCardsSection(
    modifier: Modifier,
    subjectList:List<Subject>,
    emptyListText:String="You don't have any subjects.\n" +
            "Click the + button to add new subject.",
    onAddClicked: ()->Unit,
    onSubjectCardClick:(Int?)->Unit
){
    Column(modifier=modifier) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
            ){
            Text(text = "Subjects", style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp)
            )
            IconButton(onClick = {onAddClicked()}) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Subjects")

            }

        }
        if(subjectList.isEmpty()){
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.img_books),
                contentDescription = emptyListText
            )
            Text(modifier = Modifier.fillMaxWidth(),
                text = emptyListText,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center)
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
        ) {
            items(subjectList) {subject->
                SubjectCard(
                    subjectName = subject.name,
                    gradientColor = subject.colors.map { Color(it) },
                    onClick = {onSubjectCardClick(subject.subjectId)})
            }

        }


    }
}


