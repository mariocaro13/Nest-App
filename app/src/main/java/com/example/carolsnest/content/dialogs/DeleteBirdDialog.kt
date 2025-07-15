package com.example.carolsnest.content.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DeleteBirdDialog(
    onConfirm:  () -> Unit,
    onCancel:   () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(text = "Eliminar pajaro")
        },
        text = {
            Text(text = "¿Deseas eliminar este pollo?")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(text = "Eliminar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancel
            ) {
                Text(text = "Cancelar")
            }
        }
    )
}