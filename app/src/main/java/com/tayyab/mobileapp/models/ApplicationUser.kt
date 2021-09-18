package com.tayyab.mobileapp.models

data class ApplicationUser(
    val email: String,
    val fullName: String,
    val password: String,
    val role: String,
    val userName: String
)