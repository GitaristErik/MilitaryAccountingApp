package com.example.militaryaccountingapp.di

import com.example.militaryaccountingapp.data.repository.DataRepositoryImpl
import com.example.militaryaccountingapp.domain.repository.DataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Singleton
    @Binds
    abstract fun bindHistoryRepository(impl: DataRepositoryImpl): DataRepository

}