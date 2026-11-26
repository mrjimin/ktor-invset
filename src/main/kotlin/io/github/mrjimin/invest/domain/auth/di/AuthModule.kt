package io.github.mrjimin.invest.domain.auth.di

import io.github.mrjimin.invest.domain.auth.repository.RefreshTokenRepository
import io.github.mrjimin.invest.domain.auth.repository.RefreshTokenRepositoryImpl
import io.github.mrjimin.invest.domain.auth.service.AuthService
import io.github.mrjimin.invest.domain.auth.service.AuthServiceImpl
import org.koin.dsl.module

val authModule = module {
    single<RefreshTokenRepository> {
        RefreshTokenRepositoryImpl()
    }

    single<AuthService> {
        AuthServiceImpl(
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}