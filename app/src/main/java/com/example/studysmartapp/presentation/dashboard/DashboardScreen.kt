package com.example.studysmartapp.presentation.dashboard

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHost
import com.example.studysmartapp.R
import com.example.studysmartapp.domain.model.Subject
import com.example.studysmartapp.domain.model.Task
import com.example.studysmartapp.presentation.components.CountCard
import com.example.studysmartapp.presentation.components.SubjectCard
import com.example.studysmartapp.presentation.components.TaskCheckBox
import com.example.studysmartapp.presentation.components.tasksList

@Composable
fun DashBoardScreen(){
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

    Scaffold(
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
                    subjectCount = 5,
                    studiedHours = "10",
                    goalHours = "15"
                )
            }
            item {
                SubjectsCardsSection(
                    modifier = Modifier.fillMaxWidth(),
                    subjectList =subjects )
            }
            item { 
                Button(onClick = { /*TODO*/ },
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
                onCheckBoxClick = {},
                onTaskCardClick = {}
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
            "Click the + button to add new subject."
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
            IconButton(onClick = { /*TODO*/ }) {
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
                    gradientColor = subject.colors,
                    onClick = {})
            }

        }


    }
}

