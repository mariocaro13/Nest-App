package com.example.carolsnest.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.carolsnest.R
import com.example.carolsnest.data.UserFirestoreData
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import  androidx.activity.result.ActivityResultLauncher
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit

/**
 * ProfileContent is a UI-only composable that displays the user profile details.
 *
 * It shows the profile image, display name, email, and buttons for editing the display name,
 * changing the password, and signing out.
 *
 * @param userProfile The current user profile data from Firestore.
 * @param imagePickerLauncher A launcher for picking a new profile image.
 * @param onEditDisplayName Callback invoked when the user wants to edit their display name.
 * @param onChangePassword Callback invoked when the user wants to change their password.
 * @param onSignOut Callback invoked when the user signs out.
 * @param isLoading Indicates if a backend operation is currently in progress.
 */
@Composable
fun ProfileContent(
    userProfile: UserFirestoreData,
    imagePickerLauncher: ActivityResultLauncher<String>,
    onEditDisplayName: () -> Unit,
    onChangePassword: () -> Unit,
    onSignOut: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile photo with an edit icon overlay.
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = userProfile.photoUrl ?: R.drawable.placeholder_bird,
                contentDescription = "Profile Picture",
                placeholder = painterResource(id = R.drawable.placeholder_bird),
                error = painterResource(id = R.drawable.placeholder_bird),
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier
                    .size(40.dp)
                    .offset(x = 4.dp, y = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Change Profile Picture",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        // Display name row with edit button.
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = userProfile.displayName,
                fontSize = 22.sp,
                modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onEditDisplayName) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit Display Name"
                )
            }
        }
        // Email display.
        Text(text = "Email: ${userProfile.email}", fontSize = 16.sp)
        // Button to open the change password dialog.
        Button(onClick = onChangePassword, modifier = Modifier.fillMaxWidth()) {
            Text("Change Password")
        }
        // Button to sign out.
        Button(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Sign Out")
        }
        // Optionally show a small loading indicator.
        if (isLoading) {
            Spacer(modifier = Modifier.height(8.dp))
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }
    }
}
