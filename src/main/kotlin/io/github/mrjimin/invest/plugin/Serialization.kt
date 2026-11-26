package io.github.mrjimin.invest.plugin

import io.github.mrjimin.invest.common.config.JsonConfig
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(JsonConfig.instance)
    }
}