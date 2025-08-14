package com.example.agriwork.ui.language

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

fun setLanguage(context: Context, position: Int) {
    val locale = Locale(if (position == 1) "te" else "en")
    Locale.setDefault(locale)

    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)

    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}
