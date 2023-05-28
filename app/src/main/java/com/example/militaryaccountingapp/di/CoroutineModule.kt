package com.example.militaryaccountingapp.di

import com.example.militaryaccountingapp.domain.helper.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object CoroutineModule {
    @Singleton
    @Provides
    fun provideDispatcher(): DispatcherProvider = object : DispatcherProvider {
        override fun main() = Dispatchers.Main
        override fun io() = Dispatchers.IO
        override fun default() = Dispatchers.Default
    }
}