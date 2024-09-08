package com.example.studysmartapp.presentation.session

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.studysmartapp.domain.model.Session
import com.example.studysmartapp.presentation.components.DeleteDiaLog
import com.example.studysmartapp.presentation.components.SubjectListBottomSheet
import com.example.studysmartapp.presentation.components.studySessionsList
import com.example.studysmartapp.sessions
import com.example.studysmartapp.subjects
import com.example.studysmartapp.util.Constants.ACTION_SERVICE_CANCEL
import com.example.studysmartapp.util.Constants.ACTION_SERVICE_START
import com.example.studysmartapp.util.Constants.ACTION_SERVICE_STOP
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination
@Composable
fun SessionScreenRoute(
    navigator: DestinationsNavigator
){
    val viewModel:SessionViewModel= hiltViewModel()
    SessionScreen(onBackButtonClicked = {navigator.navigateUp()})
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionScreen(
    onBackButtonClicked: () -> Unit
){
    val context= LocalContext.current

    val sheetState= rememberModalBottomSheetState()
    var isSubjectListBottomSheet by remember {
        mutableStateOf(false)
    }
    var isDeleteDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    val scope= rememberCoroutineScope()

    DeleteDiaLog(
        isOpen = isDeleteDialogOpen,
        title = "Delete Task?",
        onDismissRequest = {isDeleteDialogOpen=false },
        onConfirmButtonClick = {isDeleteDialogOpen=false},
        onBodyText = "Are you sure,you want to delete this task ? " +
                "This action can not be undone."
    )

    SubjectListBottomSheet(
        isOpen = isSubjectListBottomSheet,
        sheetState = sheetState,
        subject = subjects,
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
                    .aspectRatio(1f))
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
                            action = ACTION_SERVICE_START
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
                    }
                )
            }
            studySessionsList(
                sectionTitle = "RECENT STUDY SESSIONS",
                emptyListText = "You don't have any upcoming tasks. \n" +
                        "Click the + button in subject screen to add new task.",
                sessions = sessions,
                onDeleteClick = {isDeleteDialogOpen=true}
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
    modifier: Modifier
){
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center){
        Box(modifier = Modifier
            .size(250.dp)
            .border(5.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        )
        Text(text = "00:05:32",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp))
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
    finishButtonClick:()->Unit
){
    Row (
        modifier=modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Button(onClick = startButtonClick) {
            Text(text = "Cancel",
                modifier=Modifier
                    .padding(horizontal = 10.dp, vertical = 5.dp))
        }
        Button(onClick = cancelButtonClick) {
            Text(text = "Start",
                modifier=Modifier
                    .padding(horizontal = 10.dp, vertical = 5.dp))
        }
        Button(onClick = finishButtonClick) {
            Text(text = "Finish",
                modifier=Modifier
                    .padding(horizontal = 10.dp, vertical = 5.dp))
        }

    }
}