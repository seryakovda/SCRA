package com.example.SCRA

import android.app.Application
import android.content.Context
import com.example.tire.data.LocalSource
import com.example.tire.data.RemoteSource
import com.example.tire.data.Repository
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppEntryPoint {
    fun repository(): Repository
}
@HiltAndroidApp
class App: Application() {
    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
            val entryPoint = EntryPointAccessors.fromApplication(this@App, AppEntryPoint::class.java)
            val repository = entryPoint.repository()

            repository.clearDataStore()   // <<<<<< очищаем здесь
        }
    }
}