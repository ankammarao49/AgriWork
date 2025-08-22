package com.example.agriwork.ui.language

import android.os.Build

fun transliterateText(
    input: String,
    sourceLang: String,
    targetLang: String,
    onResult: (String) -> Unit
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // API 29+
        try {
            val id = when (targetLang) {
                "te" -> "Latin-Telugu"
                "hi" -> "Latin-Devanagari"
                "ta" -> "Latin-Tamil"
                else -> "Any-Latin" // fallback
            }

            val transliterator = android.icu.text.Transliterator.getInstance(id)
            val result = transliterator.transliterate(input)
            onResult(result)
        } catch (e: Exception) {
            e.printStackTrace()
            onResult(input)
        }
    } else {
        // Fallback for API < 29 (no ICU transliterator)
        // You can either just return the original input
        // or call your ML Kit translator here as a backup
        onResult(input)
    }
}
