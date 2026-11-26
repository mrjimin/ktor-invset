package io.github.mrjimin.invest.common.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

suspend fun <T> dbQuery(block: () -> T): T = withContext(Dispatchers.IO) {
    transaction(DatabaseFactory.getDatabase()) {
        block()
    }
}