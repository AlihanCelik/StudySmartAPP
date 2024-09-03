package com.example.studysmartapp.presentation.components

import android.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.studysmartapp.domain.model.Subject

@Composable
fun AddSubjectDialog(
    isOpen:Boolean,
    title:String="Add/Update Subject",
    onDismissRequest:()->Unit,
    onConfirmButtonClick:()->Unit,
    selectedColors:List<Color>,
    onColorChange:(List<Color>)->Unit,
    subjectName:String,
    goalHours:String,
    onSubjectNameChange:(String)->Unit,
    onGoalHoursChange:(String)->Unit

) {
    if(isOpen){
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title={ Text(text = title) },
            text = {
                Column {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        Subject.subjectsCardColors.forEach{colors->
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 1.dp,
                                        color = if (colors == selectedColors) Color.Black
                                        else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .background(brush = Brush.verticalGradient(colors))
                            )
                            OutlinedTextField(
                                value = subjectName,
                                onValueChange = onSubjectNameChange,
                                label = {
                                    Text(text = "Subject Name")
                                },
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = goalHours,
                                onValueChange = onGoalHoursChange,
                                label = {
                                    Text(text = "Goal Study Hours")
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }

                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirmButtonClick) {
                    Text(text = "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "Cancel")

                }
            }
        )
    }

}