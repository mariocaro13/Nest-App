package com.example.carolsnest.model

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carolsnest.data.UserFirestoreData
import com.example.carolsnest.imgbb.uploadImageToImgBB
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ProfileViewModel is responsible for handling all backend operations for the Profile screen.
 *
 * It loads user data from Firestore, updates the profile photo and display name,
 * changes the password, and handles sign-out.
 */
class ProfileViewModel(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {
    companion object {
        private const val TAG = "ProfileViewModel"
    }

    // Holds the current user profile.
    private val _userProfile = MutableStateFlow<UserFirestoreData?>(null)
    val userProfile: StateFlow<UserFirestoreData?> = _userProfile

    // Indicates if an asynchronous operation is in progress.
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadUserProfile()
    }

    /**
     * Loads the user's profile from Firestore.
     * If the profile document does not exist, it creates a new one.
     */
    fun loadUserProfile() {
        val currentUser = auth.currentUser ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val docRef = db.collection("users").document(currentUser.uid)
                val snapshot = docRef.get().await()
                if (snapshot.exists()) {
                    var user = snapshot.toObject(UserFirestoreData::class.java)
                    // If the Firestore photo URL differs from the current Auth profile, update it.
                    val currentPhoto = currentUser.photoUrl?.toString()
                    if (user?.photoUrl != currentPhoto && currentPhoto != null) {
                        docRef.update("photoUrl", currentPhoto).await()
                        user = user?.copy(photoUrl = currentPhoto)
                    }
                    _userProfile.value = user
                } else {
                    Log.w(TAG, "User document not found for UID: ${currentUser.uid}. Creating a new one.")
                    val newUser = UserFirestoreData(
                        uid = currentUser.uid,
                        displayName = currentUser.displayName ?: "User",
                        email = currentUser.email ?: "",
                        photoUrl = currentUser.photoUrl?.toString()
                    )
                    db.collection("users").document(currentUser.uid).set(newUser).await()
                    _userProfile.value = newUser
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching user from Firestore: ${e.message}", e)
                // Fallback: create a basic user object.
                _userProfile.value = UserFirestoreData(
                    uid = currentUser.uid,
                    displayName = currentUser.displayName ?: "User",
                    email = currentUser.email ?: "",
                    photoUrl = currentUser.photoUrl?.toString()
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Updates the profile photo by uploading the selected image to ImgBB,
     * then updating Firebase Authentication and Firestore.
     *
     * @param uri The URI of the image selected.
     * @param context The context needed for uploading the image.
     * @return The new profile image URL.
     * @throws Exception if the upload or update fails.
     */
    suspend fun updateProfilePhoto(uri: Uri, context: Context): String {
        val currentUser = auth.currentUser ?: throw Exception("No current user")
        _isLoading.value = true
        try {
            val imgBbUrl = uploadImageToImgBB(context, uri)
                ?: throw Exception("Error uploading the image to ImgBB")
            // Update Firebase Auth profile.
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setPhotoUri(imgBbUrl.toUri())
                .build()
            currentUser.updateProfile(profileUpdates).await()
            // Update Firestore document (upsert mode).
            db.collection("users").document(currentUser.uid)
                .set(mapOf("uid" to currentUser.uid, "photoUrl" to imgBbUrl), SetOptions.merge())
                .await()
            // Update the local state.
            _userProfile.value = _userProfile.value?.copy(photoUrl = imgBbUrl)
            return imgBbUrl
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Updates the display name in both Firebase Authentication and Firestore.
     *
     * @param newName The new display name.
     */
    suspend fun updateDisplayName(newName: String) {
        val currentUser = auth.currentUser ?: throw Exception("No current user")
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName.trim())
            .build()
        currentUser.updateProfile(profileUpdates).await()
        db.collection("users").document(currentUser.uid)
            .update("displayName", newName.trim())
            .await()
        _userProfile.value = _userProfile.value?.copy(displayName = newName.trim())
    }

    /**
     * Changes the user's password after re-authentication with the old password.
     *
     * @param oldPassword The current password.
     * @param newPassword The new password.
     */
    suspend fun changePassword(oldPassword: String, newPassword: String) {
        val user = auth.currentUser ?: throw Exception("No current user")
        val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
        user.reauthenticate(credential).await()
        user.updatePassword(newPassword).await()
    }

    /**
     * Signs out the current user.
     */
    fun signOut() {
        auth.signOut()
    }
}
