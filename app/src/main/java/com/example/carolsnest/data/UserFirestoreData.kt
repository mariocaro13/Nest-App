package com.example.carolsnest.data

data class UserFirestoreData(
    val uid: String = "",
    var displayName: String = "",
    val email: String = "",
    var photoUrl: String? = null,
)