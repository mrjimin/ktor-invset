package io.github.mrjimin.invest.domain.account.di

import io.github.mrjimin.invest.domain.account.repository.AccountRepository
import io.github.mrjimin.invest.domain.account.repository.AccountRepositoryImpl
import io.github.mrjimin.invest.domain.account.service.AccountService
import io.github.mrjimin.invest.domain.account.service.AccountServiceImpl
import org.koin.dsl.module

val accountModule = module {
    single<AccountRepository> {
        AccountRepositoryImpl()
    }

    single<AccountService> {
        AccountServiceImpl(get(), get())
    }
}