package io.github.mrjimin.invest.common.database

import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun <T> withTransaction(block: () -> T): T =
    transaction(
        db = DatabaseFactory.getDatabase(),
        transactionIsolation = null,
        readOnly = false,
    ) {
        block()
    }