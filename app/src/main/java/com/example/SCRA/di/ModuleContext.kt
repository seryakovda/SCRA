package com.example.SCRA.di

import android.content.Context
import com.example.tire.data.LocalSource
import dagger.hilt.components.SingletonComponent

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ModuleContext {
    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext appContext: Context): Context {
        return appContext
    }
}