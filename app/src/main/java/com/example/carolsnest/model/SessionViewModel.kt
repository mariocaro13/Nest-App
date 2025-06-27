package com.example.carolsnest.model

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SessionViewModel: ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _profileImageUrl = MutableStateFlow<String?>(auth.currentUser?.photoUrl?.toString())
    val profileImageUrl: StateFlow<String?> = _profileImageUrl

    fun updateProfileUrl(newUrl: String) {
        _profileImageUrl.value = newUrl
    }
}
