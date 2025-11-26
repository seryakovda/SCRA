package com.example.SCRA.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
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
import javax.inject.Singleton

@Singleton
class ScraDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val dataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            produceFile = { context.dataStoreFile("ItemTire.preferences_pb") }
        )

    companion object {
        private val USER_TOKEN_KEY = stringPreferencesKey("ItemTire")
    }

    suspend fun getValueCode(): Flow<String?> =
        dataStore.data
            .map { prefs -> prefs[USER_TOKEN_KEY] }
            .distinctUntilChanged()

    suspend fun setValueCode(value: String) {
        dataStore.edit { prefs ->
            prefs[USER_TOKEN_KEY] = value }
    }
}