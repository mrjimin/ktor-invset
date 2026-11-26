package io.github.mrjimin.invest.common.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.mrjimin.invest.common.config.DatabaseConfig
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult
import org.jetbrains.exposed.v1.jdbc.Database

object DatabaseFactory {
    private var dataSource: HikariDataSource? = null
    private var database: Database? = null

    fun initialize(config: DatabaseConfig) {
        check(database == null) { "Database already initialized" }

        val pool = createDataSource(config)

        try {
            migrate(pool, config.migrations)
            database = Database.connect(pool)
            dataSource = pool
        } catch (cause: Throwable) {
            pool.close()
            throw cause
        }
    }

    fun getDatabase(): Database =
        checkNotNull(database) { "Database not initialized" }

    fun close() {
        dataSource?.close()
        dataSource = null
        database = null
    }
}

private fun createDataSource(
    config: DatabaseConfig
): HikariDataSource =
    HikariDataSource(
        HikariConfig().apply {
            jdbcUrl = config.url
            username = config.username
            password = config.password
            driverClassName = config.driverClassName

            maximumPoolSize = config.maxPoolSize

            addDataSourceProperty("cachePrepStmts", config.cachePrepStmts)
            addDataSourceProperty("prepStmtCacheSize", config.prepStmtCacheSize)
            addDataSourceProperty("prepStmtCacheSqlLimit", config.prepStmtCacheSqlLimit)
        }
    )

private fun migrate(
    dataSource: HikariDataSource,
    config: DatabaseConfig.Migrations,
): MigrateResult =
    Flyway.configure()
        .dataSource(dataSource)
        .locations(config.locations)
        .baselineOnMigrate(config.baselineOnMigrate)
        .load()
        .migrate()

fun Application.configureDatabase() {
    val config = environment.config
        .config("database")
        .getAs<DatabaseConfig>()

    DatabaseFactory.initialize(config)

    monitor.subscribe(ApplicationStopped) {
        DatabaseFactory.close()
    }
}