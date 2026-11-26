package io.github.mrjimin.invest.common.config

import kotlinx.serialization.Serializable

@Serializable
data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val accessExpirationMs: Long,
    val refreshExpirationMs: Long
)