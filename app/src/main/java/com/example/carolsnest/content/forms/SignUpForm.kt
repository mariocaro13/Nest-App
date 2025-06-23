package com.example.carolsnest.content.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carolsnest.content.components.ActionButtonsRow
import com.example.carolsnest.content.components.PasswordField
import com.example.carolsnest.content.components.TextInputField
import com.example.carolsnest.model.SignUpState

@Composable
fun SignUpForm(
    state: SignUpState,
    onDisplayNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onGoBack: () -> Unit,
    onSignUp: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Crear Cuenta", fontSize = 28.sp, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(20.dp))

        TextInputField(
            value = state.displayName,
            onValueChange = onDisplayNameChange,
            label = "Nombre de Usuario",
        )
        Spacer(modifier = Modifier.height(16.dp))

        TextInputField(
            value = state.email,
            onValueChange = onEmailChange,
            label = "Correo electrónico",
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(16.dp))

        PasswordField(
            password = state.password,
            onPasswordChange = onPasswordChange,
            label = "Contraseña",
        )
        Spacer(modifier = Modifier.height(16.dp))

        PasswordField(
            password = state.confirmPassword,
            onPasswordChange = onConfirmPasswordChange,
            label = "Confirmar Contraseña",
        )
        Spacer(modifier = Modifier.height(24.dp))

        ActionButtonsRow(
            isLoading = state.isLoading,
            firstButtonText = "Iniciar sesión",
            secondButtonText = "Enviar",
            onFirstButtonClick = onGoBack,
            onSecondButtonClick = onSignUp
        )
    }
}
