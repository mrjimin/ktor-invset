package io.github.mrjimin.invest.domain.user.di

import io.github.mrjimin.invest.domain.user.repository.UserRepository
import io.github.mrjimin.invest.domain.user.repository.UserRepositoryImpl
import io.github.mrjimin.invest.domain.user.service.UserService
import io.github.mrjimin.invest.domain.user.service.UserServiceImpl
import org.koin.dsl.module

val userModule = module {
    single<UserRepository> {
        UserRepositoryImpl()
    }

    single<UserService> {
        UserServiceImpl(get(), get())
    }
}