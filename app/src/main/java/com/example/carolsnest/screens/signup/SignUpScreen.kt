package com.example.carolsnest.screens.signup

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.carolsnest.content.forms.SignUpForm
import com.example.carolsnest.model.SignUpViewModel

@Composable
fun SignUpScreen(navController: NavController, signUpViewModel: SignUpViewModel = viewModel()) {
    val state = signUpViewModel.state.value
    val context = LocalContext.current

    SignUpForm(
        state = state,
        onDisplayNameChange = { signUpViewModel.onDisplayNameChange(it) },
        onEmailChange = { signUpViewModel.onEmailChange(it) },
        onPasswordChange = { signUpViewModel.onPasswordChange(it) },
        onConfirmPasswordChange = { signUpViewModel.onConfirmPasswordChange(it) },
        onGoBack = { navController.popBackStack() },
        onSignUp = {
            signUpViewModel.signUp(onSuccess = {
                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }, onError = { errorMsg ->
                Toast.makeText(context, "Error en el registro: $errorMsg", Toast.LENGTH_LONG).show()
            })
        })
}
