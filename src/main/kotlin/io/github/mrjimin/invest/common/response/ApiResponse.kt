package io.github.mrjimin.invest.common.response

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
) {
    companion object {
        fun <T> success(
            data: T,
            message: String = "요청이 성공했습니다."
        ): ApiResponse<T> = ApiResponse(
            success = true,
            message = message,
            data = data
        )

        fun success(
            message: String = "요청이 성공했습니다."
        ): ApiResponse<Unit> = ApiResponse(
            success = true,
            message = message,
            data = null
        )

        fun failure(
            message: String
        ): ApiResponse<Unit> = ApiResponse(
            success = false,
            message = message,
            data = null
        )
    }
}