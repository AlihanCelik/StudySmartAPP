package com.example.studysmartapp.presentation.task

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studysmartapp.presentation.components.DeleteDiaLog
import com.example.studysmartapp.presentation.components.SubjectListBottomSheet
import com.example.studysmartapp.presentation.components.TaskCheckBox
import com.example.studysmartapp.presentation.components.TaskDatePicker
import com.example.studysmartapp.util.Priority
import com.example.studysmartapp.util.SnackbarEvent
import com.example.studysmartapp.util.changeMillsToDateString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant

data class TaskScreenNavArgs(
    val taskId:Int?,
    val subjectId:Int?
)

@Destination(navArgsDelegate = TaskScreenNavArgs::class)
@Composable
fun TaskScreenRoute(
    navigator: DestinationsNavigator
){
    val viewModel:TaskViewModel= hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    TaskScreen(
        snackbarEvent = viewModel.snackbarEventFlow,
        state = state,
        onEvent = viewModel::onEvent,
        onBackButtonClick = {navigator.navigateUp()})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    state: TaskState,
    snackbarEvent: SharedFlow<SnackbarEvent>,
    onEvent: (TaskEvent)->Unit,
    onBackButtonClick: () -> Unit
) {


    var isDeleteDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isDatePickerDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    val datePickerState= rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    val sheetState= rememberModalBottomSheetState()
    var isSubjectListBottomSheet by remember {
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

    val scope= rememberCoroutineScope()
    SubjectListBottomSheet(
        isOpen = isSubjectListBottomSheet,
        sheetState = sheetState,
        subject = state.subjects,
        onSubjectClicked ={
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if(!sheetState.isVisible) isSubjectListBottomSheet=false
            }
            onEvent(TaskEvent.onRelatedSubjectSelect(it))
        },
        onDismissReguest = {isSubjectListBottomSheet=false})

    TaskDatePicker(
        state = datePickerState,
        isOpen = isDatePickerDialogOpen,
        onDismissRequest = {isDatePickerDialogOpen=false},
        onConfirmButtonClicked = {
            onEvent(TaskEvent.onDateChange(mills = datePickerState.selectedDateMillis))
            isDatePickerDialogOpen=false}
        )

    DeleteDiaLog(
        isOpen = isDeleteDialogOpen,
        title = "Delete Task?",
        onDismissRequest = {isDeleteDialogOpen=false },
        onConfirmButtonClick = {
            onEvent(TaskEvent.DeleteTask)
            isDeleteDialogOpen=false},
        onBodyText = "Are you sure,you want to delete this task ? " +
                "This action can not be undone."
    )


    var taskTitleError by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    taskTitleError= when{
        state.title.isBlank()-> "Please enter task title"
        state.title.length <4 ->"Task title is too short"
        state.title.length > 30 ->"Task title is too long"
        else -> null
    }

    Scaffold(
        snackbarHost = {SnackbarHost(hostState = snackbarHostState)},
        topBar = {
            TaskScreenTopBar(
                isTaskExist = state.currentTaskId!=null,
                isComplete = state.isTaskComplete,
                checkBoxBorderColor = state.priority.color ,
                onBackButtonClick =onBackButtonClick,
                onDeleteButtonClick = { isDeleteDialogOpen=true },
                onCheckBoxClick = {onEvent(TaskEvent.onIsCompleteChange)}
            )

        }
    ) {paddingValues ->
        Column (

            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
        ){
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.title,
                onValueChange = {onEvent(TaskEvent.onTitleChange(it))},
                label = { Text(text = "Title")},
                isError = taskTitleError!=null && state.title.isNotBlank(),
                supportingText = { Text(text = taskTitleError.orEmpty())}
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value =state.description,
                onValueChange ={onEvent(TaskEvent.onDescriptionChange(it))} ,
                label = { Text(text = "Description")}
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Due Date",
                style = MaterialTheme.typography.bodySmall
            )
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = state.dueDate.changeMillsToDateString(),
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = {isDatePickerDialogOpen=true}) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Select Due Date")

                }

            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Priority",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row (modifier = Modifier.fillMaxWidth()){
                Priority.entries.forEach{priority->
                    PriorityButton(
                        modifier = Modifier.weight(1f),
                        label = priority.title,
                        backgroundColor = priority.color,
                        borderColor = if(priority==state.priority){
                            Color.White
                        }else Color.Transparent,
                        labelColor = if(priority==state.priority){
                            Color.White
                        }else Color.White.copy(alpha = 0.7f),
                        onClick = {onEvent(TaskEvent.onPriorityChange(priority))}
                    )
                }

            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "Related to subject",
                style = MaterialTheme.typography.bodySmall
            )
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                val firstSubject=state.subjects.firstOrNull()?.name?:""
                Text(text = state.relatedToSubject?:firstSubject,
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = { isSubjectListBottomSheet=true}) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select Subject")

                }

            }
            Button(
                enabled = taskTitleError==null, 
                onClick = { onEvent(TaskEvent.SaveTask)
                          },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                Text(text = "Save")
                
            }

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreenTopBar(
    isTaskExist:Boolean,
    isComplete:Boolean,
    checkBoxBorderColor: Color,
    onBackButtonClick:()->Unit,
    onDeleteButtonClick:()->Unit,
    onCheckBoxClick:()->Unit
){
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Navigate Back")
            }
        },
        title = { Text(text = "Task")},
        actions = {
            if(isTaskExist){
                TaskCheckBox(
                    isComplete = isComplete,
                    borderColor = checkBoxBorderColor,
                    onCheckBoxClick = onCheckBoxClick
                )
                IconButton(onClick = onDeleteButtonClick) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Task")

                }
            }
        }
    )

}

@Composable
private fun PriorityButton(
    modifier: Modifier=Modifier,
    label:String,
    backgroundColor:Color,
    borderColor:Color,
    labelColor: Color,
    onClick:()->Unit
){
    Box (
        modifier= modifier
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(5.dp)
            .border(1.dp, borderColor, RoundedCornerShape(5.dp))
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ){
        Text(text = label, color = labelColor)

    }
}