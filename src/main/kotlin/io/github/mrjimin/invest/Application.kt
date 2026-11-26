package io.github.mrjimin.invest

import io.github.mrjimin.invest.common.database.configureDatabase
import io.github.mrjimin.invest.plugin.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureDatabase()
    configureKoin()
    configureSecurity()
    configureStatusPages()
    configureWebsockets()
    configureRouting()
}