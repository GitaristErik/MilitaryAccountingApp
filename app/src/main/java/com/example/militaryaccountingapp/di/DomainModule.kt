package com.example.militaryaccountingapp.di


import com.example.militaryaccountingapp.domain.repository.AuthRepository
import com.example.militaryaccountingapp.domain.repository.DataRepository
import com.example.militaryaccountingapp.domain.usecase.GetHistoryUseCase
import com.example.militaryaccountingapp.domain.usecase.auth.CurrentUserUseCase
import com.example.militaryaccountingapp.domain.usecase.auth.LoginUseCase
import com.example.militaryaccountingapp.domain.usecase.auth.LogoutUseCase
import com.example.militaryaccountingapp.domain.usecase.auth.RegisterUseCase
import com.example.militaryaccountingapp.domain.usecase.auth.ResetPasswordUseCase
import com.example.militaryaccountingapp.domain.usecase.auth.SignInFacebookUseCase
import com.example.militaryaccountingapp.domain.usecase.auth.SignInGoogleUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {

    @Provides
    fun provideGetHistoryUseCase(
        repository: DataRepository
    ) = GetHistoryUseCase(repository)

    @Provides
    fun provideCurrentUserUseCase(
        repository: AuthRepository
    ) = CurrentUserUseCase(repository)

    @Provides
    fun provideLoginUseCase(
        repository: AuthRepository
    ) = LoginUseCase(repository)

    @Provides
    fun provideLogoutUseCase(
        repository: AuthRepository
    ) = LogoutUseCase(repository)

    @Provides
    fun provideRegisterUseCase(
        repository: AuthRepository
    ) = RegisterUseCase(repository)

    @Provides
    fun provideResetPasswordUseCase(
        repository: AuthRepository
    ) = ResetPasswordUseCase(repository)

    @Provides
    fun provideSignInFacebookUseCase(
        repository: AuthRepository
    ) = SignInFacebookUseCase(repository)

    @Provides
    fun provideSignInGoogleUseCase(
        repository: AuthRepository
    ) = SignInGoogleUseCase(repository)

}