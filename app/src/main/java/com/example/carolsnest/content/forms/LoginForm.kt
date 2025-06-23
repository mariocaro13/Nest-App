package com.example.carolsnest.content.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carolsnest.content.components.ActionButtonsRow
import com.example.carolsnest.content.components.PasswordField
import com.example.carolsnest.content.components.TextInputField

/**
 * LoginForm is a presentational composable that displays the login form UI.
 *
 * This component shows email and password text fields, along with "Register" and "Send" buttons,
 * and delegates changes and actions to the provided callbacks.
 *
 * @param email The current value of the email text field.
 * @param password The current value of the password text field.
 * @param isLoading Indicates whether authentication is in progress.
 * @param onEmailChange Callback invoked when the email input changes.
 * @param onPasswordChange Callback invoked when the password input changes.
 * @param onLogin Callback invoked when the "Send" button is pressed.
 * @param onSignUpClick Callback invoked when the "Register" button is pressed.
 */
@Composable
fun LoginForm(
    email: String,
    password: String,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Inicio de Sesión",
            fontSize = 28.sp,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextInputField(value = email, onValueChange = onEmailChange)
        Spacer(modifier = Modifier.height(16.dp))

        PasswordField(password = password, onPasswordChange = onPasswordChange)
        Spacer(modifier = Modifier.height(16.dp))

        ActionButtonsRow(
            isLoading = isLoading,
            firstButtonText = "Registrarse",
            secondButtonText = "Enviar",
            onFirstButtonClick = onSignUpClick,
            onSecondButtonClick = onLogin
        )
    }
}
