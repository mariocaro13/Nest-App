package com.example.carolsnest.model

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carolsnest.data.BirdData
import com.example.carolsnest.navigation.AppDestinations
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BirdDetailViewModel(
    savedStateHandle: SavedStateHandle,
    firestore: FirebaseFirestore,
) : ViewModel() {

    private val birdId: String = savedStateHandle.get<String>(AppDestinations.BIRD_ID_ARG) ?: ""

    private val _birdData = MutableStateFlow<BirdData?>(null)
    val birdData: StateFlow<BirdData?> = _birdData.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val birdDocRef =
        if (birdId.isNotEmpty()) firestore.collection("birds").document(birdId) else null

    init {
        Log.d("BirdDetailVW_Init: ", "Firestore - birdId from SavedStateHandle: '$birdId'")
        if (birdId.isNotEmpty() && birdDocRef != null) {
            viewModelScope.launch {
                fetchBirdDetailsOnce()
            }
        } else {
            _errorMessage.value = "ID de pájaro no válido."
            _isLoading.value = false
            Log.e(
                "BirDetailVM_Init: ",
                "Firestore - No se llama a fetchBirdDetailsOnce. birdId: '$birdId', birdDocRef es null: ${birdDocRef == null}"
            )
        }
    }

    // Opens the "Add Bird" dialog by resetting related state.
    fun onOpenEditBirdDialog() {
    }

    private suspend fun fetchBirdDetailsOnce() {
        Log.d("fetchBirdDetailsOnce: ", "Firestore - fetchBirdDetailsOnce called")
        try {
            Log.d(
                "fetchBirdDetailsOnce: ",
                "Firestore - fetchBirdDetailsOnce - Intentando obtener documento de Firestore..."
            )
            val documentSnapshot = birdDocRef?.get()?.await()
            Log.d(
                "fetchBirdDetailsOnce: ",
                "Firestore - fetchBirdDetailsOnce - Documento obtenido: $documentSnapshot"
            )

            if (documentSnapshot != null && documentSnapshot.exists()) {
                val bird = documentSnapshot.toObject(BirdData::class.java)
                if (bird != null) {
                    _birdData.value = bird.copy(id = documentSnapshot.id)
                    Log.d(
                        "fetchBirdDetailsOnce: ",
                        "Firestore - fetchBirdDetailsOnce: Pajaro deserializado: ${bird.name}"
                    )
                } else {
                    _errorMessage.value = "No se pudo obtener los detalles del pájaro."
                    Log.e(
                        "fetchBirdDetailsOnce: ",
                        "Firestore - fetchBirdDetailsOnce: Error al deserializar: $birdId"
                    )
                }
            } else {
                _errorMessage.value = "No se encontró el pájaro con ID: $birdId"
                Log.e(
                    "fetchBirdDetailsOnce: ",
                    "Firestore - fetchBirdDetailsOnce: No se encontró el pájaro con ID: $birdId"
                )
            }

        } catch (e: Exception) {
            Log.e(
                "fetchBirdDetailsOnce: ",
                "Firestore - fetchBirdDetailsOnce: Error al obtener detalles del pájaro",
                e
            )
        } finally {
            Log.d("fetchBirdDetailsOnce: ", "Firestore - fetchBirdDetailsOnce: Finalizando")
            _isLoading.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.e("BirdDetailViewModel onCleared(): ", "Firestore - onCleared() called")
    }
}