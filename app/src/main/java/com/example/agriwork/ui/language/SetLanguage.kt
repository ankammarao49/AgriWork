package com.example.agriwork.ui.language

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

fun setLanguage(context: Context, position: Int) {
    val languageCode = when (position) {
        0 -> "en" // English
        1 -> "te" // Telugu
        2 -> "hi" // Hindi
        else -> "en" // fallback
    }

    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)

    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}
