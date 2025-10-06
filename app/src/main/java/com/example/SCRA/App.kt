package com.example.SCRA

import android.app.Application
import android.content.Context
import com.example.tire.data.LocalSource
import com.example.tire.data.RemoteSource
import dagger.Provides
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltAndroidApp
class App: Application() {

}