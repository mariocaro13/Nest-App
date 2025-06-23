package com.example.carolsnest.model

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// UI state for the Sign Up screen.
data class SignUpState(
    val displayName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SignUpViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    // Mutable state for the UI.
    var state = mutableStateOf(SignUpState())
        private set

    fun onDisplayNameChange(newName: String) {
        state.value = state.value.copy(displayName = newName)
    }

    fun onEmailChange(newEmail: String) {
        state.value = state.value.copy(email = newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        state.value = state.value.copy(password = newPassword)
    }

    fun onConfirmPasswordChange(newPassword: String) {
        state.value = state.value.copy(confirmPassword = newPassword)
    }

    /**
     * Executes the sign-up process:
     * - Validates that all fields are filled, that passwords match, and that the password meets the minimum length.
     * - Creates the user with Firebase Authentication.
     * - Updates the user's displayName in Auth.
     * - Saves user information in Firestore.
     *
     * @param onSuccess Called when the sign-up is successful.
     * @param onError Called with an error message (in Spanish) upon failure.
     */
    fun signUp(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (state.value.displayName.isBlank() || state.value.email.isBlank() || state.value.password.isBlank() || state.value.confirmPassword.isBlank()) {
            onError("Por favor, completa todos los campos")
            return
        }
        if (state.value.password != state.value.confirmPassword) {
            onError("Las contraseñas no coinciden")
            return
        }
        if (state.value.password.length < 6) {
            onError("La contraseña debe tener al menos 6 caracteres")
            return
        }

        // Set loading state.
        state.value = state.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                // 1. Create user in Firebase Authentication.
                val authResult = auth.createUserWithEmailAndPassword(
                    state.value.email.trim(), state.value.password.trim()
                ).await()
                Log.d("SignUpViewModel", "createUserWithEmail: success")
                val user = authResult.user

                // 2. Update displayName in Firebase Authentication.
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(state.value.displayName.trim()).build()
                user?.updateProfile(profileUpdates)?.await()

                // 3. Save user information in Firestore.
                if (user != null) {
                    val userDocument = hashMapOf(
                        "uid" to user.uid,
                        "displayName" to state.value.displayName.trim(),
                        "email" to user.email?.lowercase(),
                        "createdAt" to FieldValue.serverTimestamp(),
                        "photoUrl" to null
                    )
                    db.collection("users").document(user.uid).set(userDocument).await()
                    Log.d("SignUpViewModel", "User document created/updated in Firestore.")
                }

                onSuccess()
            } catch (e: Exception) {
                Log.w("SignUpViewModel", "SignUp or Firestore write failure", e)
                val errorMessage = e.message ?: "Unknown error during sign up."
                onError(errorMessage)
            } finally {
                state.value = state.value.copy(isLoading = false)
            }
        }
    }
}

