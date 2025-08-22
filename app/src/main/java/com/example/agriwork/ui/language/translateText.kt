package com.example.agriwork.ui.language

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

fun translateText(
    input: String,
    sourceLang: String,
    targetLang: String,
    onResult: (String) -> Unit
) {
    val options = TranslatorOptions.Builder()
        .setSourceLanguage(sourceLang)
        .setTargetLanguage(targetLang)
        .build()
    val translator = Translation.getClient(options)

    // Download model if not available
    translator.downloadModelIfNeeded()
        .addOnSuccessListener {
            translator.translate(input)
                .addOnSuccessListener { translatedText ->
                    onResult(translatedText)
                }
                .addOnFailureListener {
                    onResult(input) // fallback to original text
                }
        }
        .addOnFailureListener {
            onResult(input) // fallback to original text
        }
}
