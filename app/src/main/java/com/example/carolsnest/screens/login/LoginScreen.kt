package com.example.carolsnest.screens.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.carolsnest.content.forms.LoginForm
import com.example.carolsnest.model.LoginViewModel
import com.example.carolsnest.navigation.AppDestinations

/***
 * Login Screen acts as the container for the login process.
 *
 * This composable observes reactive states from the [com.example.carolsnest.model.LoginViewModel] and delegates the
 * UI rendering to [LoginForm]. It also handles navigation using the provided [NavController].
 *
 * @param navController The navigation controller used for navigating between screens.
 * @param viewModel Instance of the [com.example.carolsnest.model.LoginViewModel] used for managing login-related data and logic. Injected via Hilt.
 */
@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = viewModel()) {
    // Collect the reactive states from the ViewModel
    val email by viewModel.emailFlow.collectAsState()
    val password by viewModel.passwordFlow.collectAsState()
    val isLoading by viewModel.isLoadingFlow.collectAsState()

    // Render the login form UI, passing the state values and action callbacks.
    LoginForm(
        email = email,
        password = password,
        isLoading = isLoading,
        onEmailChange = { viewModel.onEmailChange(it) },
        onPasswordChange = { viewModel.onPasswordChange(it) },
        onLogin = { handleLogin(viewModel, navController) },
        onSignUpClick = { navigateToSignUp(navController) })
}

/**
 * Initiates the login process and handles navigation upon success.
 *
 * This helper function is invoked when the "Send" button is pressed.
 *
 * @param viewModel The [LoginViewModel] managing authentication.
 * @param navController The navigation controller used for screen transitions.
 */
private fun handleLogin(viewModel: LoginViewModel, navController: NavController) {
    viewModel.login(onSuccess = { navigateToHome(navController) }, onError = { errorMessage -> })
}

/**
 * Navigates to the home screen (HOME_SCREEN) and clears the back stack.
 *
 * @param navController The navigation controller.
 */
private fun navigateToHome(navController: NavController) {
    navController.navigate(AppDestinations.HOME_SCREEN) {
        popUpTo(navController.graph.startDestinationId) { inclusive = true }
        launchSingleTop = true
    }
}

/**
 * Navigates to the sign-up screen (SIGNUP_SCREEN).
 *
 * @param navController The navigation controller.
 */
private fun navigateToSignUp(navController: NavController) {
    navController.navigate(AppDestinations.SIGNUP_SCREEN)
}