package com.example.carolsnest.screens.profile

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.carolsnest.content.ProfileContent
import com.example.carolsnest.content.dialogs.ChangePasswordDialog
import com.example.carolsnest.content.dialogs.EditDisplayNameDialog
import com.example.carolsnest.factory.ProfileViewModelFactory
import com.example.carolsnest.model.ProfileViewModel
import com.example.carolsnest.model.SessionViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

/**
 * ProfileScreen is the container composable for the user profile.
 *
 * It gathers state from [com.example.carolsnest.model.ProfileViewModel], triggers backend operations and displays dialogs
 * for editing the display name and changing the password.
 *
 * @param navController Navigation controller.
 */
@Composable
fun ProfileScreen(navController: NavController, sessionVm: SessionViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val factor = ProfileViewModelFactory(auth, db)
    val viewModel: ProfileViewModel = viewModel(factory = factor)

    // Collect backend state.
    val isLoading by viewModel.isLoading.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val currentUser = FirebaseAuth.getInstance().currentUser


    // Local state for showing dialogs.
    var showEditDisplayNameDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    // Image picker launcher used for changing the profile photo.
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            scope.launch {
                try {
                    val newUrl = viewModel.updateProfilePhoto(selectedUri, context)
                    Toast.makeText(
                        context, "Profile photo updated successfully", Toast.LENGTH_SHORT
                    ).show()
                    sessionVm.updateProfileUrl(newUrl)
                } catch (e: Exception) {
                    Log.e("ProfileScreen", "Error updating profile photo: ${e.message}", e)
                    Toast.makeText(context, "Error updating photo: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    when {
        isLoading && userProfile == null -> {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        currentUser == null || userProfile == null -> {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text("Could not load user profile.")
            }
        }

        else -> {
            ProfileContent(
                userProfile = userProfile!!,
                imagePickerLauncher = imagePickerLauncher,
                onEditDisplayName = { showEditDisplayNameDialog = true },
                onChangePassword = { showChangePasswordDialog = true },
                onSignOut = {
                    scope.launch {
                        viewModel.signOut()
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                },
                isLoading = isLoading
            )
        }
    }

    // Minimal implementation of an edit display name dialog.
    if (showEditDisplayNameDialog) {
        EditDisplayNameDialog(
            currentName = userProfile?.displayName ?: "",
            onDismiss = { showEditDisplayNameDialog = false },
            onConfirm = { newName ->
                if (newName.isNotBlank() && newName != userProfile?.displayName) {
                    val profile = userProfile ?: return@EditDisplayNameDialog
                    if (newName.isNotBlank() && newName != profile.displayName) {
                        scope.launch {
                            try {
                                viewModel.updateDisplayName(newName)
                                Toast.makeText(context, "Display name updated", Toast.LENGTH_SHORT)
                                    .show()
                                showEditDisplayNameDialog = false
                            } catch (e: Exception) {
                                Log.e(
                                    "ProfileScreen", "Error updating display name: ${e.message}", e
                                )
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }
                } else if (newName.isBlank()) {
                    Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                } else {
                    showEditDisplayNameDialog = false
                }
            })
    }

    // Minimal implementation of a change password dialog.
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onConfirm = { oldPassword, newPassword ->
                if (oldPassword.isBlank() || newPassword.isBlank()) {
                    Toast.makeText(context, "Please complete all fields", Toast.LENGTH_SHORT).show()
                    return@ChangePasswordDialog
                }
                if (newPassword.length < 6) {
                    Toast.makeText(
                        context, "New password must be at least 6 characters", Toast.LENGTH_SHORT
                    ).show()
                    return@ChangePasswordDialog
                }
                scope.launch {
                    try {
                        viewModel.changePassword(oldPassword, newPassword)
                        Toast.makeText(context, "Password updated", Toast.LENGTH_SHORT).show()
                        showChangePasswordDialog = false
                    } catch (e: Exception) {
                        Log.e("ProfileScreen", "Error changing password: ${e.message}", e)
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            })
    }
}
