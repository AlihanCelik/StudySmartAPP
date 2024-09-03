package com.example.studysmartapp.presentation.components

import android.app.AlertDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun AddSubjectDialog(
    isOpen:Boolean,
    title:String="Add/Update Subject",
    onDismissRequest:()->Unit,
    onConfirmButtonClick:()->Unit,

) {
    if(isOpen){
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title={ Text(text = title) },
            text = {
                Text(text = "Dialog Body")
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