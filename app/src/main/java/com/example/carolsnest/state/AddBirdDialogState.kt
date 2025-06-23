package com.example.carolsnest.state

data class AddBirdDialogState(
    val birdName: String = "",
    val birdDescription: String = "",
    val isUploading: Boolean = false,
    val isUploadingImage: Boolean = false,
    val errorMessage: String? = null
)