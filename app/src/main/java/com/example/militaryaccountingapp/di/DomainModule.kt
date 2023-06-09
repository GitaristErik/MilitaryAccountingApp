package com.example.militaryaccountingapp.di


import com.example.militaryaccountingapp.domain.repository.DataRepository
import com.example.militaryaccountingapp.domain.usecase.GetHistoryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {

    @Provides
    fun provideCalculateExpressionUseCase(
        repository: DataRepository
    ) = GetHistoryUseCase(repository)

}