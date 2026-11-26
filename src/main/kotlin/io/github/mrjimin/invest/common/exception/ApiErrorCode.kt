package io.github.mrjimin.invest.common.exception

import io.ktor.http.HttpStatusCode

enum class ApiErrorCode(
    val status: HttpStatusCode,
    val message: String
) {
    INVALID_REQUEST(
        HttpStatusCode.BadRequest,
        "잘못된 요청입니다."
    ),

    USER_NOT_FOUND(
        HttpStatusCode.NotFound,
        "사용자를 찾을 수 없습니다."
    ),

    USERNAME_ALREADY_EXISTS(
        HttpStatusCode.Conflict,
        "이미 사용 중인 아이디입니다."
    ),

    STUDENT_NUMBER_REQUIRED(
        HttpStatusCode.BadRequest,
        "학번을 입력해 주세요."
    ),

    INVALID_STUDENT_NUMBER(
        HttpStatusCode.BadRequest,
        "올바르지 않은 학번입니다."
    ),

    STUDENT_NUMBER_ALREADY_EXISTS(
        HttpStatusCode.Conflict,
        "이미 등록된 학번입니다."
    ),

    UNAUTHORIZED(
        HttpStatusCode.Unauthorized,
        "인증에 실패했습니다."
    ),

    INTERNAL_SERVER_ERROR(
        HttpStatusCode.InternalServerError,
        "서버 내부 오류가 발생했습니다."
    ),

    INVALID_CREDENTIALS(
        HttpStatusCode.Unauthorized,
        "아이디 또는 비밀번호가 올바르지 않습니다."
    ),

    INVALID_REFRESH_TOKEN(
        HttpStatusCode.Unauthorized,
        "Refresh Token이 유효하지 않거나 만료되었습니다."
    ),

    FORBIDDEN(
        HttpStatusCode.Forbidden,
        "접근 권한이 없습니다."
    ),

    ACCOUNT_NOT_FOUND(
        HttpStatusCode.NotFound,
        "계좌를 찾을 수 없습니다."
    ),

    STOCK_NOT_FOUND(
        HttpStatusCode.NotFound,
        "종목을 찾을 수 없습니다."
    ),

    STOCK_CODE_ALREADY_EXISTS(
        HttpStatusCode.Conflict,
        "이미 존재하는 종목 코드입니다."
    ),
}