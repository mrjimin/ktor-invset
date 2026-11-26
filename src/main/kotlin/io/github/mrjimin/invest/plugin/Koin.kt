package io.github.mrjimin.invest.plugin

import io.github.mrjimin.invest.common.config.JwtConfig
import io.github.mrjimin.invest.common.security.di.securityModule
import io.github.mrjimin.invest.domain.account.di.accountModule
import io.github.mrjimin.invest.domain.auth.di.authModule
import io.github.mrjimin.invest.domain.user.di.userModule
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    val jwtConfig = environment.config
        .config("jwt")
        .getAs<JwtConfig>()

    install(Koin) {
        slf4jLogger()
        modules(
            securityModule(jwtConfig),
            userModule,
            authModule,
            accountModule,
        )
    }
}