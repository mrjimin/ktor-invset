package io.github.mrjimin.invest.common.config

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseConfig(
    val driverClassName: String,
    val url: String,
    val username: String,
    val password: String,
    val maxPoolSize: Int,
    val cachePrepStmts: Boolean,
    val prepStmtCacheSize: Int,
    val prepStmtCacheSqlLimit: Int,
    val migrations: Migrations,
) {
    @Serializable
    data class Migrations(
        val locations: String,
        val baselineOnMigrate: Boolean,
    )
}