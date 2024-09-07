package com.example.studysmartapp.presentation.subject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studysmartapp.domain.model.Subject
import com.example.studysmartapp.presentation.components.AddSubjectDialog
import com.example.studysmartapp.presentation.components.CountCard
import com.example.studysmartapp.presentation.components.DeleteDiaLog
import com.example.studysmartapp.presentation.components.studySessionsList
import com.example.studysmartapp.presentation.components.tasksList
import com.example.studysmartapp.presentation.destinations.TaskScreenRouteDestination
import com.example.studysmartapp.presentation.task.TaskScreenNavArgs
import com.example.studysmartapp.sessions
import com.example.studysmartapp.tasks
import com.example.studysmartapp.util.SnackbarEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

data class SubjectScreenNavArgs(
    val subjectId: Int
)

@Destination(navArgsDelegate = SubjectScreenNavArgs::class)
@Composable
fun SubjectScreenRoute(
    navigator: DestinationsNavigator
){
    val viewModel:SubjectViewModel= hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    SubjectScreen(
        state =state,
        snackbarEvent = viewModel.snackbarEventFlow,
        onEvent = viewModel::onEvent,
        onBackButtonClick = { navigator.navigateUp()},
        onAddTaskButtonClick = {
            val navArg= TaskScreenNavArgs(taskId=null, subjectId = -1)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))
        },
        onTaskCardClick = {taskId->
            val navArg=TaskScreenNavArgs(taskId=taskId, subjectId = null)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))

        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(
    state: SubjectState,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    onEvent: (SubjectEvent)->Unit,
    onBackButtonClick: () -> Unit,
    onAddTaskButtonClick:() -> Unit,
    onTaskCardClick:(Int?) -> Unit
){

    val scrollBehavior=TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState= rememberLazyListState()
    val isFabExpanded by remember {
        derivedStateOf { listState.firstVisibleItemIndex==0 }
    }



    var isEditSubjectDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var isDeleteSubjectDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var isDeleteSessionDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val snackbarHostState=remember{ SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        snackbarEvent.collectLatest { event->
            when(event){
                is SnackbarEvent.ShowSnackbar->{
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }

                SnackbarEvent.NavigateUp -> {
                    onBackButtonClick()
                }
            }
        }

    }

    LaunchedEffect(key1 = state.studiedHours, key2 = state.goalStudyHours) {
        onEvent(SubjectEvent.UpdateProgress)

    }
    AddSubjectDialog(
        isOpen = isEditSubjectDialog,
        goalHours=state.goalStudyHours,
        subjectName = state.subjectName,
        selectedColors = state.subjectCardColors,
        onSubjectNameChange = {onEvent(SubjectEvent.onSubjectNameChange(it))},
        onGoalHoursChange = {onEvent(SubjectEvent.onGoalStudyHoursChange(it))},
        onColorChange = {onEvent(SubjectEvent.onSubjectCardColorChange(it))},
        onDismissRequest = { isEditSubjectDialog=false },
        onConfirmButtonClick = {
            onEvent(SubjectEvent.UpdateSubject)
            isEditSubjectDialog=false
        })
    DeleteDiaLog(
        isOpen = isDeleteSubjectDialog,
        title = "Delete Session1",
        onBodyText = "Are you sure, you want to delete this subject? All related" +
                "tasks and study sessions will be permanently removed.This action can not be undone",
        onDismissRequest = {isDeleteSubjectDialog=false},
        onConfirmButtonClick = {
            onEvent(SubjectEvent.DeleteSubject)
            isDeleteSubjectDialog=false

        }
    )
    DeleteDiaLog(
        isOpen = isDeleteSessionDialog,
        title = "Delete Session1",
        onBodyText = "Are you sure, you want to delete this subject? All related" +
                "tasks and study sessions will be permanently removed.This action can not be undone",
        onDismissRequest = {isDeleteSessionDialog=false},
        onConfirmButtonClick = {
            onEvent(SubjectEvent.DeleteSessipn)
            isDeleteSessionDialog=false}
    )
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState)},
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { SubjectScreenTopBar(
            title = state.subjectName,
            onBackButton = onBackButtonClick,
            onDeleteButton = {isDeleteSubjectDialog=true},
            onEditButtonClick = {isEditSubjectDialog=true},
            scrollBehavior=scrollBehavior
        )},
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTaskButtonClick,
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add")},
                text = { Text(text = "Add Task")},
                expanded = isFabExpanded
            )
        }
    ) {paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
            item {
                SubjectOverviewSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    studiedHours = state.studiedHours.toString(), goalHours = state.goalStudyHours, progress =state.progress)
            }
            tasksList(
                sectionTitle = "UPCOMING TASKS",
                emptyListText = "You don't have any upcoming tasks.\n" +
                        "Click the + button in subject screen to add new task.",
                tasks =state.upcomingTasks,
                onCheckBoxClick = {onEvent(SubjectEvent.onTaskIsComplteChange(it))},
                onTaskCardClick = onTaskCardClick
            )
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            tasksList(
                sectionTitle = "COMPLETED TASKS",
                emptyListText = "You don't have any upcoming tasks.\n" +
                        "Click the + button in subject screen to add new task.",
                tasks =state.completedTasks,
                onCheckBoxClick = {onEvent(SubjectEvent.onTaskIsComplteChange(it))},
                onTaskCardClick = onTaskCardClick
            )
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            studySessionsList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = "You don't have any upcoming tasks. \n" +
                        "Click the + button in subject screen to add new task.",
                sessions = state.recentSessions,
                onDeleteClick = {
                    onEvent(SubjectEvent.onDeleteSessionButtonClick(it))
                    isDeleteSessionDialog=true}
            )

        }
        
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectScreenTopBar(

    title:String,
    onBackButton:()->Unit,
    onDeleteButton:()->Unit,
    onEditButtonClick:()->Unit,
    scrollBehavior: TopAppBarScrollBehavior
){
    LargeTopAppBar(
        scrollBehavior=scrollBehavior,
        navigationIcon = {
            IconButton(onClick = onBackButton) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "navigation back")

            }
        }
        , title = {
            Text(text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall)
        },
        actions = {
            IconButton(onClick = onDeleteButton) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Subject")

            }
            IconButton(onClick = onEditButtonClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Subject")

            }
        }
    )
}

@Composable
private fun SubjectOverviewSection(
    modifier: Modifier,
    studiedHours:String,
    goalHours:String,
    progress:Float
){
    val percentageProgress= remember (progress){
        (progress*100).toInt().coerceIn(0,100)
    }
    Row(
        modifier=modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ){
        CountCard(modifier = Modifier.weight(1f), headingText = "Goal Study Hours", count =studiedHours )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(modifier = Modifier.weight(1f), headingText = "Study Hours", count = goalHours)
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier.size(75.dp),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = progress,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round
            )
            Text(text = "${percentageProgress}%")
        }
    }
}