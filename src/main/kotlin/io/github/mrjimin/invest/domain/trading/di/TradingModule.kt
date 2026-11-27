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

    single<TaxRepository> {
        TaxRepositoryImpl()
    }

    single {
        TradingFeeCalculator()
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
            taxRepository = get(),
            timeProvider = get(),
            tradingCalendar = get(),
            holdingProcessor = get(),
            tradingFeeCalculator = get()
        )
    }

    single {
        PendingOrderProcessor(
            accountRepository = get(),
            stockRepository = get(),
            orderRepository = get(),
            transactionRepository = get(),
            taxRepository = get(),
            timeProvider = get(),
            tradingCalendar = get(),
            holdingProcessor = get(),
            holdingRepository = get(),
            tradingFeeCalculator = get()
        )
    }

    single {
        OrderCancellationProcessor(
            accountRepository = get(),
            orderRepository = get(),
            timeProvider = get()
        )
    }

    single<OrderService> {
        OrderServiceImpl(
            orderRepository = get(),
            placementProcessor = get(),
            pendingOrderProcessor = get(),
            cancellationProcessor = get()
        )
    }

    single<TradingQueryService> {
        TradingQueryServiceImpl(get(), get())
    }

    single {
        TradingScheduler(get())
    }
}
