package com.example.carolsnest.content.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.carolsnest.content.components.PasswordField

/**
 * A minimal dialog for changing the password.
 *
 * @param onDismiss Called when the dialog is dismissed.
 * @param onConfirm Called with the old and new passwords if confirmed.
 */
@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                PasswordField(
                    password = oldPassword,
                    onPasswordChange = { oldPassword = it},
                    label = "Old Password",
                )
                Spacer(modifier = Modifier.height(8.dp))
                PasswordField(
                    password = newPassword,
                    onPasswordChange = { newPassword = it},
                    label = "New Password",
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(oldPassword, newPassword) }) { Text("Change") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
