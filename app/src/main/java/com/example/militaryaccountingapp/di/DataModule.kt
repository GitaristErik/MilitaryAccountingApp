package com.example.militaryaccountingapp.di

import com.example.militaryaccountingapp.data.repository.AuthRepositoryImpl
import com.example.militaryaccountingapp.data.repository.CategoryRepositoryImpl
import com.example.militaryaccountingapp.data.repository.DataRepositoryImpl
import com.example.militaryaccountingapp.data.repository.HistoryRepositoryImpl
import com.example.militaryaccountingapp.data.repository.ItemRepositoryImpl
import com.example.militaryaccountingapp.data.repository.PermissionRepositoryImpl
import com.example.militaryaccountingapp.data.repository.UserRepositoryImpl
import com.example.militaryaccountingapp.data.storage.AuthStorage
import com.example.militaryaccountingapp.data.storage.Storage
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.repository.AuthRepository
import com.example.militaryaccountingapp.domain.repository.CategoryRepository
import com.example.militaryaccountingapp.domain.repository.DataRepository
import com.example.militaryaccountingapp.domain.repository.HistoryRepository
import com.example.militaryaccountingapp.domain.repository.ItemRepository
import com.example.militaryaccountingapp.domain.repository.PermissionRepository
import com.example.militaryaccountingapp.domain.repository.UserRepository
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
    abstract fun bindDataRepository(impl: DataRepositoryImpl): DataRepository

    @Singleton
    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository

    @Singleton
    @Binds
    abstract fun bindItemRepository(impl: ItemRepositoryImpl): ItemRepository

    @Singleton
    @Binds
    abstract fun bindHistoryRepository(impl: HistoryRepositoryImpl): HistoryRepository

    @Singleton
    @Binds
    abstract fun bindPermissionRepository(impl: PermissionRepositoryImpl): PermissionRepository

    @Singleton
    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    abstract fun bindAuthStorage(impl: AuthStorage): Storage<String, User>

}