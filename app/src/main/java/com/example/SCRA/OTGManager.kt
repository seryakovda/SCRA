package com.example.SCRA

import android.provider.Settings
import android.content.ContentResolver

class OTGManager {

    fun enableOTG(contentResolver: ContentResolver) {
        try {
            // Ключ может отличаться в зависимости от производителя
            // Common keys for OTG setting
            val otgSetting = Settings.System.getString(contentResolver, "otg_connection")

            // Установить значение 1 для включения
            val success = Settings.System.putInt(
                contentResolver,
                "otg_connection",
                1
            )

            if (success) {
                println("OTG включен")
            } else {
                println("Не удалось включить OTG")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disableOTG(contentResolver: ContentResolver) {
        Settings.System.putInt(contentResolver, "otg_connection", 0)
    }

    fun isOTGEnabled(contentResolver: ContentResolver): Boolean {
        return Settings.System.getInt(contentResolver, "otg_connection", 0) == 1
    }
}