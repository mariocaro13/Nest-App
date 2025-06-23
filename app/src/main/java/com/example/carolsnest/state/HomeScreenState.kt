package com.example.carolsnest.state

import android.net.Uri
import com.example.carolsnest.data.BirdData

data class HomeScreenState(
    val birds: List<BirdData> = emptyList(),
    val isLoading: Boolean = false,
    val showAddBirdDialog: Boolean = false,
    val addBirdDialogState: AddBirdDialogState = AddBirdDialogState(),
    val selectedImageUrisForPreview: List<Uri> = emptyList()
)
