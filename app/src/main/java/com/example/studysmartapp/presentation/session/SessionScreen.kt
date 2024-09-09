package com.example.studysmartapp.presentation.session

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studysmartapp.presentation.components.DeleteDiaLog
import com.example.studysmartapp.presentation.components.SubjectListBottomSheet
import com.example.studysmartapp.presentation.components.studySessionsList
import com.example.studysmartapp.ui.theme.Red
import com.example.studysmartapp.util.Constants.ACTION_SERVICE_CANCEL
import com.example.studysmartapp.util.Constants.ACTION_SERVICE_START
import com.example.studysmartapp.util.Constants.ACTION_SERVICE_STOP
import com.example.studysmartapp.util.SnackbarEvent
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Duration

@Destination(
    deepLinks = [
        DeepLink(
            action = Intent.ACTION_VIEW,
            uriPattern = "study_smart://dashboard/session"
        )
    ]
)
@Composable
fun SessionScreenRoute(
    navigator: DestinationsNavigator,
    timerService:StudyServiceTimerService
){
    val viewModel:SessionViewModel= hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    SessionScreen(
        state = state,
        onEvent = viewModel::onEvent, onBackButtonClicked = {navigator.navigateUp()},
        timerService=timerService,
        snackbarEvent = viewModel.snackbarEventFlow)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionScreen(
    snackbarEvent: SharedFlow<SnackbarEvent>,
    state: SessionState,
    onEvent: (SessionEvent)->Unit,
    onBackButtonClicked: () -> Unit,
    timerService: StudyServiceTimerService
){
    val hours by timerService.hours
    val minutes by timerService.minutes
    val seconds by timerService.seconds
    val currenctTimerState by timerService.currentTimerState


    val context= LocalContext.current

    val sheetState= rememberModalBottomSheetState()
    var isSubjectListBottomSheet by remember {
        mutableStateOf(false)
    }
    var isDeleteDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    val scope= rememberCoroutineScope()
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

                SnackbarEvent.NavigateUp ->{
                }
            }
        }

    }

    DeleteDiaLog(
        isOpen = isDeleteDialogOpen,
        title = "Delete Task?",
        onDismissRequest = {isDeleteDialogOpen=false },
        onConfirmButtonClick = {
            onEvent(SessionEvent.DeleteSession)
            isDeleteDialogOpen=false},
        onBodyText = "Are you sure,you want to delete this task ? " +
                "This action can not be undone."
    )

    SubjectListBottomSheet(
        isOpen = isSubjectListBottomSheet,
        sheetState = sheetState,
        subject = state.subjects,
        onSubjectClicked ={
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if(!sheetState.isVisible) isSubjectListBottomSheet=false
            }
        },
        onDismissReguest = {isSubjectListBottomSheet=false})

    Scaffold (
        topBar = {SessionScreenTopBar(onBackButtonClicked = onBackButtonClicked)}
    ){paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                TimerSection(modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                    minutes=minutes,
                    hours = hours,
                    seconds = seconds)
            }
            item {
                RelatedToSubjectSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    relatedToSubject = "English",
                    selectSubjectButtonClick = {isSubjectListBottomSheet=true}
                )
            }
            item{
                ButtonsSection(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    startButtonClick = {
                        ServiceHelper.triggerForegroundService(
                            context = context,
                            action = if (currenctTimerState== TimerState.STARTED){
                                ACTION_SERVICE_STOP
                                }else ACTION_SERVICE_START
                        )
                    },
                    cancelButtonClick = {
                        ServiceHelper.triggerForegroundService(
                            context = context,
                            action = ACTION_SERVICE_CANCEL
                        )
                    },
                    finishButtonClick = {
                        ServiceHelper.triggerForegroundService(
                            context = context,
                            action = ACTION_SERVICE_STOP
                        )
                    },
                    timerState = currenctTimerState,
                    seconds = seconds
                )
            }
            studySessionsList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = "You don't have any upcoming tasks. \n" +
                        "Click the + button in subject screen to add new task.",
                sessions = state.sessions,
                onDeleteClick = {
                    onEvent(SessionEvent.onDeleteSessionButtonClick(it))
                    isDeleteDialogOpen=true}
            )

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionScreenTopBar(
    onBackButtonClicked:()->Unit
){
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackButtonClicked) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Navigation to Back Screen")

            }
        },
        title = {
            Text(text = "Study Sessions", style = MaterialTheme.typography.headlineSmall)
        })

}

@Composable
private fun TimerSection(
    modifier: Modifier,
    hours:String,
    minutes:String,
    seconds:String
){
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center){
        Box(modifier = Modifier
            .size(250.dp)
            .border(5.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        )
        Row{
            AnimatedContent(targetState = hours, label = hours, transitionSpec = { timerTextAnimation() }) { hours->
                Text(text = "${hours}:", style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp))
            }
            AnimatedContent(targetState = minutes, label = minutes, transitionSpec = { timerTextAnimation() }) {minutes->
                Text(text = "${minutes}:", style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp))
            }
            AnimatedContent(targetState = seconds, label = seconds, transitionSpec = { timerTextAnimation() }) {seconds->
                Text(text = seconds, style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp))
            }
        }
    }
}

@Composable
private fun RelatedToSubjectSection(
    modifier: Modifier,
    relatedToSubject:String,
    selectSubjectButtonClick:()->Unit
){
    Column(
        modifier=modifier
    ) {
        Text(text = "Related to subject",
            style = MaterialTheme.typography.bodySmall
        )
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(text = "English",
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = { selectSubjectButtonClick()}) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Subject")

            }

        }

    }

}

@Composable
private fun ButtonsSection(
    modifier: Modifier,
    startButtonClick:()->Unit,
    cancelButtonClick:()->Unit,
    finishButtonClick:()->Unit,
    timerState:TimerState,
    seconds: String
){
    Row (
        modifier=modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Button(onClick = cancelButtonClick,
            enabled = seconds!= "00" && timerState==TimerState.STARTED) {
            Text(text = "Cancel",
                modifier=Modifier
                    .padding(horizontal = 10.dp, vertical = 5.dp))
        }
        Button(onClick = startButtonClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if(timerState==TimerState.STARTED) Red
                else MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )) {
            Text(text = when(timerState){
                TimerState.STARTED->"Stop"
                TimerState.STOPPED->"Resume"
                else->"Start"
            },
                modifier=Modifier
                    .padding(horizontal = 10.dp, vertical = 5.dp))
        }
        Button(onClick = finishButtonClick,enabled = seconds!= "00" && timerState==TimerState.STARTED) {
            Text(text = "Finish",
                modifier=Modifier
                    .padding(horizontal = 10.dp, vertical = 5.dp))
        }

    }
}

private fun timerTextAnimation(duration: Int=600):ContentTransform{
    return slideInVertically(animationSpec = tween(duration)){fullHeight ->fullHeight} +
    fadeIn(animationSpec = tween(duration)) togetherWith
    slideOutVertically(animationSpec = tween(duration)){fullHeight ->fullHeight  }+
    fadeOut(animationSpec = tween(duration))


}