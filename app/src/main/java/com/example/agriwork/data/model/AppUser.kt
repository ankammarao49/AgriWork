package com.example.agriwork.data.model

data class AppUser(
    val uid: String = "",
    val name: String = "",
    val role: String = "", // either "farmer" or "worker"
    val location: String = "",
    val mobileNumber: String = ""
)