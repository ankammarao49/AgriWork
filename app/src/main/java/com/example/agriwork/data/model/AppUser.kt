package com.example.agriwork.data.model

data class MultiLangField(
    val en: String,
    val te: String,
    val hi: String,
    val ta: String
)

//data class AppUser(
//    val uid: String,
//    val name: MultiLangField,
//    val location: MultiLangField,
//    val role: MultiLangField,
//    val mobileNumber: String = ""
//)

data class AppUser(
    val uid: String = "",
    val name: String = "",
    val role: String = "", // either "farmer" or "worker"
    val location: String = "",
    val mobileNumber: String = ""
)