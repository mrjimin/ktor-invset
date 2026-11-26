package io.github.mrjimin.invest.common.security.di

import io.github.mrjimin.invest.common.config.JwtConfig
import io.github.mrjimin.invest.common.security.BCryptPasswordEncoder
import io.github.mrjimin.invest.common.security.PasswordEncoder
import io.github.mrjimin.invest.common.security.jwt.JwtProvider
import org.koin.dsl.module

fun securityModule(jwtConfig: JwtConfig) = module {
    single { JwtProvider(jwtConfig) }

    single<PasswordEncoder> {
        BCryptPasswordEncoder()
    }
}