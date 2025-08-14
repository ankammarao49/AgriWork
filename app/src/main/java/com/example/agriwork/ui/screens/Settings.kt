package com.example.agriwork.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agriwork.data.repository.DataStorePreferenceRepository
import com.example.agriwork.ui.language.DataStoreViewModelFactory
import com.example.agriwork.ui.language.LanguageViewModel
import com.example.agriwork.ui.language.ToggleGroup
import com.example.agriwork.ui.language.setLanguage
import kotlinx.coroutines.launch
import com.example.agriwork.R
import com.example.agriwork.ui.language.LanguageDropdown

@Composable
fun SettingsScreen(
    viewModel: LanguageViewModel = viewModel(
        factory = DataStoreViewModelFactory(
            DataStorePreferenceRepository.getInstance(LocalContext.current)
        )
    )
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentLanguage by viewModel.language.observeAsState(initial = 0) // ✅ Fixed

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            LanguageDropdown(currentLanguage) { selected ->
                scope.launch {
                    viewModel.saveLanguage(selected) // Save in DataStore
                    setLanguage(context, selected)    // Apply immediately
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
            Text(
                text = stringResource(id = R.string.settings_sample_text),
                textAlign = TextAlign.Center
            )
        }
    }
}
