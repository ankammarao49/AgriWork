package com.example.agriwork.ui.language

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agriwork.data.repository.DataStorePreferenceRepository
import kotlinx.coroutines.launch

@Composable
fun LanguageSelector() {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()

    val viewModel: LanguageViewModel = viewModel(
        factory = DataStoreViewModelFactory(
            DataStorePreferenceRepository.getInstance(context)
        )
    )

    val currentLanguage by viewModel.language.observeAsState(initial = 0)

    LanguageDropdown(currentLanguage) { selected ->
        scope.launch {
            // Save in DataStore
            viewModel.saveLanguage(selected)

            // Apply language change
            setLanguage(context, selected)

            // Force UI reload so new strings are applied
            activity?.recreate()
        }
    }
}