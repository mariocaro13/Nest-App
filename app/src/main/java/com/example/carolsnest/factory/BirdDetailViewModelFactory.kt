package com.example.carolsnest.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.carolsnest.model.BirdDetailViewModel
import com.google.firebase.firestore.FirebaseFirestore

class BirdDetailViewModelFactory(
    private val firestore: FirebaseFirestore,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras
    ): T {
        if (modelClass.isAssignableFrom(BirdDetailViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            return BirdDetailViewModel(savedStateHandle, firestore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
