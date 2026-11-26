package io.github.mrjimin.invest.domain.trading.di

import io.github.mrjimin.invest.common.time.AdjustableTimeProvider
import io.github.mrjimin.invest.common.time.TimeProvider
import io.github.mrjimin.invest.common.time.TradingCalendar
import io.github.mrjimin.invest.domain.trading.repository.*
import io.github.mrjimin.invest.domain.trading.service.*
import org.koin.dsl.module

val tradingModule = module {

    single<TimeProvider> {
        AdjustableTimeProvider()
    }

    single {
        TradingCalendar(get())
    }

    single<HoldingRepository> {
        HoldingRepositoryImpl()
    }

    single<OrderRepository> {
        OrderRepositoryImpl()
    }

    single<TransactionRepository> {
        TransactionRepositoryImpl()
    }

    single {
        HoldingProcessor(get())
    }

    single {
        OrderPlacementProcessor(
            accountRepository = get(),
            stockRepository = get(),
            holdingRepository = get(),
            orderRepository = get(),
            transactionRepository = get(),
            timeProvider = get(),
            tradingCalendar = get(),
            holdingProcessor = get()
        )
    }

    single {
        PendingOrderProcessor(
            accountRepository = get(),
            stockRepository = get(),
            orderRepository = get(),
            transactionRepository = get(),
            timeProvider = get(),
            tradingCalendar = get(),
            holdingProcessor = get(),
            holdingRepository = get()
        )
    }

    single<OrderService> {
        OrderServiceImpl(get(), get(), get())
    }

    single<TradingQueryService> {
        TradingQueryServiceImpl(get(), get())
    }
}
