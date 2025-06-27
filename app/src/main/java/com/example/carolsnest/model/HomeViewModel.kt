package com.example.carolsnest.model

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carolsnest.content.components.BottomTab
import com.example.carolsnest.data.BirdData
import com.example.carolsnest.imgbb.uploadImageToImgBB
import com.example.carolsnest.state.AddBirdDialogState
import com.example.carolsnest.state.HomeScreenState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    // Aggregated UI state for the home screen.
    private val homeScreenStateMutableStateFlow = MutableStateFlow(HomeScreenState())
    val homeScreenStateStateFlow: StateFlow<HomeScreenState> =
        homeScreenStateMutableStateFlow.asStateFlow()

    // Internal set to store uploaded image URLs (from ImgBB)
    private val _uploadedImgBbUrlsSet = mutableSetOf<String>()

    private val _currentTab: MutableStateFlow<BottomTab> = MutableStateFlow(BottomTab.Home)
    val currentTab: StateFlow<BottomTab> = _currentTab.asStateFlow()

    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl: StateFlow<String?> = _profileImageUrl.asStateFlow()

    init {
        loadUserBirds()
        loadUserProfileUrl()
    }

    fun onTabSelected(tab: BottomTab) {
        _currentTab.value = tab
    }

    private fun loadUserProfileUrl() {
        auth.currentUser?.photoUrl?.toString()?.let { _profileImageUrl.value = it }
    }

    fun refreshProfileImageUrl() = loadUserProfileUrl()

    // Opens the "Add Bird" dialog by resetting related state.
    fun onOpenAddBirdDialog() {
        homeScreenStateMutableStateFlow.update { currentState ->
            currentState.copy(
                showAddBirdDialog = true,
                addBirdDialogState = AddBirdDialogState(),
                selectedImageUrisForPreview = emptyList()
            )
        }
        _uploadedImgBbUrlsSet.clear()
    }

    // Closes the "Add Bird" dialog.
    fun onDismissAddBirdDialog() {
        homeScreenStateMutableStateFlow.update { currentState ->
            currentState.copy(showAddBirdDialog = false)
        }
    }

    fun onBirdNameChange(name: String) {
        homeScreenStateMutableStateFlow.update { currentState ->
            currentState.copy(
                addBirdDialogState = currentState.addBirdDialogState.copy(
                    birdName = name, errorMessage = null
                )
            )
        }
    }

    fun onBirdDescriptionChange(description: String) {
        homeScreenStateMutableStateFlow.update { currentState ->
            currentState.copy(
                addBirdDialogState = currentState.addBirdDialogState.copy(
                    birdDescription = description, errorMessage = null
                )
            )
        }
    }

    // Called when an image is selected; uploads the image to ImgBB and updates the state accordingly.
    fun onImageSelected(uri: Uri, context: Context) {
        // Ensure the image is not already selected and that we don't exceed a maximum of 5 images.
        homeScreenStateMutableStateFlow.update { currentState ->
            if (!currentState.selectedImageUrisForPreview.contains(uri) && currentState.selectedImageUrisForPreview.size < 5) {
                currentState.copy(
                    selectedImageUrisForPreview = currentState.selectedImageUrisForPreview + uri,
                    addBirdDialogState = currentState.addBirdDialogState.copy(
                        isUploadingImage = true, errorMessage = null
                    )
                )
            } else currentState
        }
        viewModelScope.launch {
            try {
                // Upload the image to ImgBB
                val imageUrl = uploadImageToImgBB(context, uri, maxSizeKb = 1024)
                if (imageUrl != null) {
                    _uploadedImgBbUrlsSet.add(imageUrl)
                } else {
                    Log.e("HomeViewModel", "ImgBB upload returned null for $uri")
                    homeScreenStateMutableStateFlow.update { currentState ->
                        currentState.copy(
                            addBirdDialogState = currentState.addBirdDialogState.copy(errorMessage = "Error al subir la imagen a ImgBB.")
                        )
                    }
                    homeScreenStateMutableStateFlow.update { currentState ->
                        currentState.copy(
                            selectedImageUrisForPreview = currentState.selectedImageUrisForPreview - uri
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception while uploading image to ImgBB: $uri", e)
                homeScreenStateMutableStateFlow.update { currentState ->
                    currentState.copy(
                        addBirdDialogState = currentState.addBirdDialogState.copy(errorMessage = "Error al subir la imagen a ImgBB.")
                    )
                }
                homeScreenStateMutableStateFlow.update { currentState ->
                    currentState.copy(
                        selectedImageUrisForPreview = currentState.selectedImageUrisForPreview - uri
                    )
                }
            } finally {
                homeScreenStateMutableStateFlow.update { currentState ->
                    currentState.copy(
                        addBirdDialogState = currentState.addBirdDialogState.copy(isUploadingImage = false)
                    )
                }
            }
        }
    }

    fun onImageRemoved(uri: Uri) {
        homeScreenStateMutableStateFlow.update { currentState ->
            currentState.copy(selectedImageUrisForPreview = currentState.selectedImageUrisForPreview - uri)
        }
    }

    // Checks in Firestore if a bird with the given name exists for the current user.
    private suspend fun checkIfBirdNameExists(userId: String, name: String): Boolean {
        if (name.isBlank()) return false
        val querySnapshot =
            db.collection("birds").whereEqualTo("userId", userId).whereEqualTo("name", name.trim())
                .limit(1).get().await()
        return !querySnapshot.isEmpty
    }

    // Saves a new bird in Firestore using the information from the dialog.
    fun saveBird() {
        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            homeScreenStateMutableStateFlow.update { currentState ->
                currentState.copy(
                    addBirdDialogState = currentState.addBirdDialogState.copy(
                        errorMessage = "Error: El usuario debe estar autenticado.",
                        isUploading = false
                    )
                )
            }
            return
        }
        val currentDialogState = homeScreenStateMutableStateFlow.value.addBirdDialogState
        val birdNameTrimmed = currentDialogState.birdName.trim()
        if (birdNameTrimmed.isBlank()) {
            homeScreenStateMutableStateFlow.update { currentState ->
                currentState.copy(
                    addBirdDialogState = currentState.addBirdDialogState.copy(
                        errorMessage = "Error: El nombre del pollito no puede estar vacío.",
                        isUploading = false
                    )
                )
            }
            return
        }
        homeScreenStateMutableStateFlow.update { currentState ->
            currentState.copy(
                addBirdDialogState = currentState.addBirdDialogState.copy(
                    isUploading = true, errorMessage = null
                )
            )
        }
        viewModelScope.launch {
            if (checkIfBirdNameExists(currentUserId, birdNameTrimmed)) {
                homeScreenStateMutableStateFlow.update { currentState ->
                    currentState.copy(
                        addBirdDialogState = currentState.addBirdDialogState.copy(
                            isUploading = false,
                            errorMessage = "Error: El nombre del pollito ya existe."
                        )
                    )
                }
                return@launch
            }
            if (homeScreenStateMutableStateFlow.value.addBirdDialogState.isUploadingImage) {
                homeScreenStateMutableStateFlow.update { currentState ->
                    currentState.copy(
                        addBirdDialogState = currentState.addBirdDialogState.copy(
                            isUploading = false,
                            errorMessage = "Error: Fallo al subir las imágenes."
                        )
                    )
                }
                return@launch
            }
            val finalImageUrlsToSave = _uploadedImgBbUrlsSet.toList()
            if (homeScreenStateMutableStateFlow.value.selectedImageUrisForPreview.isNotEmpty() && finalImageUrlsToSave.isEmpty()) {
                homeScreenStateMutableStateFlow.update { currentState ->
                    currentState.copy(
                        addBirdDialogState = currentState.addBirdDialogState.copy(
                            isUploading = false,
                            errorMessage = "Error: Fallo al subir las imágenes."
                        )
                    )
                }
                return@launch
            }

            val newBirdId = db.collection("birds").document().id
            val birdDataToSave = BirdData(
                id = newBirdId,
                name = birdNameTrimmed,
                description = homeScreenStateMutableStateFlow.value.addBirdDialogState.birdDescription.trim()
                    .takeIf { it.isNotBlank() },
                imageUrls = finalImageUrlsToSave,
                timestamp = System.currentTimeMillis(),
                userId = currentUserId
            )

            try {
                withContext(Dispatchers.IO) {
                    db.collection("birds").document(newBirdId).set(birdDataToSave).await()
                }
                Log.d("HomeViewModel", "Bird saved successfully")
                onDismissAddBirdDialog()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error saving bird", e)
                homeScreenStateMutableStateFlow.update { currentState ->
                    currentState.copy(
                        addBirdDialogState = currentState.addBirdDialogState.copy(
                            isUploading = false, errorMessage = "Error: ${e.message}"
                        )
                    )
                }
            } finally {
                homeScreenStateMutableStateFlow.update { currentState ->
                    currentState.copy(
                        addBirdDialogState = currentState.addBirdDialogState.copy(isUploading = false)
                    )
                }
            }
        }
    }

    // Loads the list of birds for the current user from Firestore.
    private fun loadUserBirds() {
        val currentUser = auth.currentUser
        homeScreenStateMutableStateFlow.update { currentState ->
            currentState.copy(isLoading = true)
        }
        if (currentUser != null) {
            db.collection("birds").whereEqualTo("userId", currentUser.uid)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshots, e ->
                    homeScreenStateMutableStateFlow.update { currentState ->
                        currentState.copy(isLoading = false)
                    }
                    if (e != null) {
                        Log.w("HomeViewModel", "Listen failed.", e)
                        homeScreenStateMutableStateFlow.update { currentState ->
                            currentState.copy(birds = emptyList())
                        }
                        return@addSnapshotListener
                    }
                    if (snapshots != null) {
                        val birdList = snapshots.documents.mapNotNull { document ->
                            document.toObject(BirdData::class.java)?.copy(id = document.id)
                        }
                        homeScreenStateMutableStateFlow.update { currentState ->
                            currentState.copy(birds = birdList)
                        }
                    } else {
                        homeScreenStateMutableStateFlow.update { currentState ->
                            currentState.copy(birds = emptyList())
                        }
                    }
                }
        } else {
            homeScreenStateMutableStateFlow.update { currentState ->
                currentState.copy(birds = emptyList(), isLoading = false)
            }
        }
    }
}
