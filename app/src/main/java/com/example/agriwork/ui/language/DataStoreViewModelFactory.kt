package com.example.agriwork.ui.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.agriwork.data.repository.DataStorePreferenceRepository

class DataStoreViewModelFactory(
    private val dataStorePreferenceRepository: DataStorePreferenceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LanguageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LanguageViewModel(dataStorePreferenceRepository) as T
        }
        throw IllegalStateException("Unknown ViewModel class")
    }
}
