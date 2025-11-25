package com.example.SCRA.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.SCRA.myLog
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ScraDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("ItemTire")
        private val USER_TOKEN_KEY = stringPreferencesKey("ItemTire")
    }

    // Читать обычную строку
    suspend fun getValueCode(): Flow<String?> =
        context.dataStore.data
            .map { prefs -> prefs[USER_TOKEN_KEY] }
            .distinctUntilChanged()

    // Записать обычную строку
    suspend fun setValueCode(value: String) {
        myLog.e("UsbForegroundService_DataStore", "WRITE: $value")
        context.dataStore.edit { prefs ->
            prefs[USER_TOKEN_KEY] = value
        }
    }
}