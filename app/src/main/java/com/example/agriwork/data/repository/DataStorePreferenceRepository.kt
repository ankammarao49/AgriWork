package com.example.agriwork.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "LanguageData")

class DataStorePreferenceRepository private constructor(private val context: Context) {

    private val defaultLanguage = 0

    companion object {
        val PREF_LANGUAGE = intPreferencesKey("language")

        @Volatile
        private var INSTANCE: DataStorePreferenceRepository? = null

        fun getInstance(context: Context): DataStorePreferenceRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DataStorePreferenceRepository(context).also { INSTANCE = it }
            }
        }
    }

    suspend fun setLanguage(language: Int) {
        context.dataStore.edit { preferences ->
            preferences[PREF_LANGUAGE] = language
        }
    }

    val getLanguage: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PREF_LANGUAGE] ?: defaultLanguage
        }
}
