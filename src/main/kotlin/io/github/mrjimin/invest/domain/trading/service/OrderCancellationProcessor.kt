package io.github.mrjimin.invest.domain.trading.service

import io.github.mrjimin.invest.common.exception.ApiErrorCode
import io.github.mrjimin.invest.common.exception.ApiException
import io.github.mrjimin.invest.common.time.TimeProvider
import io.github.mrjimin.invest.domain.account.repository.AccountRepository
import io.github.mrjimin.invest.domain.trading.model.Order
import io.github.mrjimin.invest.domain.trading.model.OrderSide
import io.github.mrjimin.invest.domain.trading.model.OrderStatus
import io.github.mrjimin.invest.domain.trading.repository.OrderRepository

internal class OrderCancellationProcessor(
    private val accountRepository: AccountRepository,
    private val orderRepository: OrderRepository,
    private val timeProvider: TimeProvider
) {

    fun cancelOrder(
        userId: Long,
        orderId: Long
    ): Order {
        val order = orderRepository.findByIdForUpdate(orderId)
            ?: throw ApiException(
                ApiErrorCode.INVALID_REQUEST,
                "주문을 찾을 수 없습니다."
            )

        if (order.userId != userId) {
            throw ApiException(
                ApiErrorCode.INVALID_REQUEST,
                "주문을 찾을 수 없습니다."
            )
        }

        if (order.status != OrderStatus.PENDING) {
            throw ApiException(
                ApiErrorCode.INVALID_REQUEST,
                "대기 중인 주문만 취소할 수 있습니다."
            )
        }

        val now = timeProvider.nowUtc()

        if (order.side == OrderSide.BUY) {
            refundBuyOrder(order, now)
        }

        check(
            orderRepository.updateStatus(
                id = order.id,
                status = OrderStatus.CANCELED,
                executedPrice = null,
                executedAt = null,
                cancelledAt = now,
                feeAmount = 0L,
                taxAmount = 0L
            )
        ) {
            "주문 취소에 실패했습니다. orderId=${order.id}"
        }

        return order.copy(
            status = OrderStatus.CANCELED,
            executedPrice = null,
            executedAt = null,
            cancelledAt = now,
            feeAmount = 0L,
            taxAmount = 0L
        )
    }

    private fun refundBuyOrder(
        order: Order,
        now: kotlinx.datetime.LocalDateTime
    ) {
        val account = accountRepository.findByUserIdForUpdate(order.userId)
            ?: throw ApiException(ApiErrorCode.ACCOUNT_NOT_FOUND)

        val refundAmount = order.reservedAmount

        if (refundAmount <= 0L) {
            error(
                "잘못된 예약금입니다. orderId=${order.id}"
            )
        }

        if (account.reservedBalance < refundAmount) {
            error(
                "예약금이 부족합니다. orderId=${order.id}"
            )
        }

        val newReservedBalance = Math.subtractExact(
            account.reservedBalance,
            refundAmount
        )

        check(
            accountRepository.updateBalances(
                userId = account.userId,
                balance = account.balance,
                reservedBalance = newReservedBalance,
                updatedAt = now
            )
        ) {
            "매수 주문 예약금 반환에 실패했습니다. orderId=${order.id}"
        }
    }
}
