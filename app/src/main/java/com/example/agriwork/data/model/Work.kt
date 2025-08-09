package com.example.agriwork.data.model

import java.util.UUID

data class Work(
    val id: String = UUID.randomUUID().toString(),
    val farmer: AppUser = AppUser(),
    val workTitle: String = "",
    val daysRequired: Int = 0,
    val acres: Double = 0.0,
    val workersNeeded: Int = 0,
    val workersSelected: List<String>? = null,
    val workersApplied: List<String>? = null,
)
