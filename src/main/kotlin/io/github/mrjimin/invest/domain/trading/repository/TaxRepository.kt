package io.github.mrjimin.invest.domain.trading.repository

import io.github.mrjimin.invest.domain.trading.model.TaxCollection

interface TaxRepository {
    fun save(taxCollection: TaxCollection): TaxCollection
    fun findAll(): List<TaxCollection>
    fun sumTaxAmount(): Long
}