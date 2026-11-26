package io.github.mrjimin.invest.common.config

import kotlinx.serialization.json.Json

object JsonConfig {
    val instance = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
}