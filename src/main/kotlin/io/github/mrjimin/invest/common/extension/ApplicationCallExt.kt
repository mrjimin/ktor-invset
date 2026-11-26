package io.github.mrjimin.invest.common.extension

import io.ktor.server.application.*
import io.ktor.server.plugins.*

fun ApplicationCall.requireLongParameter(name: String): Long =


    parameters[name]?.toLongOrNull()
        // ?.takeIf { it > 0 }
        ?: throw BadRequestException("Invalid parameter: $name")

