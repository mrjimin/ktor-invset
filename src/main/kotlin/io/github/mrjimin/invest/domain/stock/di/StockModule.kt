package io.github.mrjimin.invest.domain.stock.di

import io.github.mrjimin.invest.domain.stock.repository.StockPriceRepository
import io.github.mrjimin.invest.domain.stock.repository.StockPriceRepositoryImpl
import io.github.mrjimin.invest.domain.stock.repository.StockRepository
import io.github.mrjimin.invest.domain.stock.repository.StockRepositoryImpl
import io.github.mrjimin.invest.domain.stock.service.StockPriceService
import io.github.mrjimin.invest.domain.stock.service.StockPriceServiceImpl
import io.github.mrjimin.invest.domain.stock.service.StockService
import io.github.mrjimin.invest.domain.stock.service.StockServiceImpl
import org.koin.dsl.module

val stockModule = module {
    single<StockRepository> {
        StockRepositoryImpl()
    }

    single<StockService> {
        StockServiceImpl(get(), get())
    }

    single<StockPriceRepository> {
        StockPriceRepositoryImpl()
    }

    single<StockPriceService> {
        StockPriceServiceImpl(get(), get(), get())
    }
}