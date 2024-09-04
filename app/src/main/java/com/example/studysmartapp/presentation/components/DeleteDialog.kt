package com.example.studysmartapp.presentation.components

import android.health.connect.changelog.ChangeLogsResponse.DeletedLog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.room.Delete

@Composable
fun DeleteDiaLog(
    isOpen:Boolean,
    title:String,
    onDismissRequest:()->Unit,
    onBodyText:String,
    onConfirmButtonClick:()->Unit
){
    if(isOpen){
        AlertDialog(
            title = {
                Text(text = title)
            },
            text = {
                Text(text = onBodyText)
            },
            onDismissRequest =onDismissRequest, 
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "Cancel")
                    
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirmButtonClick) {
                    Text(text = "Delete")
                    
                }
            },
            )
    }

}