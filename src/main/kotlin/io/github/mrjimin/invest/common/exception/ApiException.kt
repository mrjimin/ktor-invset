package io.github.mrjimin.invest.common.exception

class ApiException(
    val errorCode: ApiErrorCode,
    override val message: String = errorCode.message
) : RuntimeException(message)