package com.example.carolsnest.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * LoginViewModel handles the login process using FirebaseAuth.
 *
 * It exposes three StateFlows representing:
 * - The email input.
 * - The password input.
 * - The loading state during authentication.
 *
 * The [login] function performs authentication via FirebaseAuth and relays the result
 * through success and error callbacks.
 */
class LoginViewModel : ViewModel() {
    companion object {
        private const val TAG = "LoginViewModel"
    }

    // FirebaseAuth instance used for logging in the user.
    private val auth: FirebaseAuth = Firebase.auth

    // MutableStateFlow holding the email input.
    private val _email = MutableStateFlow("")
    val emailFlow: StateFlow<String> = _email

    // MutableStateFlow holding the password input.
    private val _password = MutableStateFlow("")
    val passwordFlow: StateFlow<String> = _password

    // MutableStateFlow indicating whether a login operation is underway.
    private val _isLoading = MutableStateFlow(false)
    val isLoadingFlow: StateFlow<Boolean> = _isLoading

    /**
     * Updates the email state with the new input.
     *
     * @param newEmail The new email input provided by the user.
     */
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    /**
     * Updates the password state with the new input.
     *
     * @param newPassword The new password input provided by the user.
     */
    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    /**
     * Validates that both the email and password fields are not blank.
     *
     * @return True if valid credentials are provided.
     */
    private fun isValidCredentials(): Boolean {
        return _email.value.isNotBlank() && _password.value.isNotBlank()
    }

    /**
     * Performs the login action using FirebaseAuth.
     *
     * This function is responsible for the actual authentication process and is called
     * by [login] once the credentials have been validated.
     *
     * @throws Exception if authentication fails.
     */
    private suspend fun performLogin() {
        auth.signInWithEmailAndPassword(
            _email.value.trim(), _password.value.trim()
        ).await()
    }

    /**
     * Initiates login using FirebaseAuth.
     *
     * The function checks that both email and password are not blank, shows a loading indicator,
     * and calls either [onSuccess] or [onError] based on the authentication result.
     *
     * @param onSuccess Callback invoked when authentication is successful.
     * @param onError Callback receiving an error message if authentication fails.
     */
    fun login(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (isValidCredentials()) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    performLogin()
                    _isLoading.value = false
                    onSuccess()
                } catch (e: Exception) {
                    _isLoading.value = false
                    Log.e(TAG, "Error during sign in", e)
                    onError(e.message ?: "Unknown error")
                }
            }
        } else {
            onError("Por favor, ingrese un correo electrónico y contraseña válidos.")
        }
    }
}