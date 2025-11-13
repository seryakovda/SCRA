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

class ScraDataStore @Inject constructor(@ApplicationContext
                                         private val context: Context,
){
    companion object {

        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("ItemTire")
        private val USER_TOKEN_KEY = stringPreferencesKey("ItemTire")

    }

    val getAccessToken: Flow<ScraList?> = context.dataStore.data.map {
        preferences ->
        preferences[USER_TOKEN_KEY]?.let {
            Json.decodeFromString<ScraList>(it)
        }
    }

    suspend fun saveToken(tireList:ScraList?) {
        val vv = Json.encodeToString(tireList)
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = vv
        }
    }

    suspend fun setValueCode(value: String) {
        try {
            val token = getAccessToken.first() // Берет только первое значение и завершает
            if (token != null) {
                token.code = value
                saveToken(token)
            } else {
                saveToken(
                    ScraList(
                        typeCode = "123",
                        code = value
                    )
                )
            }
        } catch (e: Exception) {
            myLog.e("UsbForegroundService", "Error: " + e.message)
        }
    }

//    suspend fun setValueCode(value: String) {
//        var TireList = getAccessToken
//            .take(1)
//            .collect { token ->
//                if (token != null) {
//                    token.code = value
//                    saveToken(token)
//                } else {
//                    saveToken(
//                        ScraList(
//                            typeCode = "123",
//                            code = value
//                        )
//                    )
//                }
//            }
//    }
    suspend fun getValueCode(): Flow<ScraList?> =
        context.dataStore.data.map { Json.decodeFromString(it[USER_TOKEN_KEY] ?: "null") }

    suspend fun setValueTypeCode(value:String){
        var TireList = getAccessToken.collect{
            if (it != null) {
                it.typeCode = value
                saveToken(it)
            }
        }
    }

}