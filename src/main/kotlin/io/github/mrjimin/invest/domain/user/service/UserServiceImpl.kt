package io.github.mrjimin.invest.domain.user.service

import io.github.mrjimin.invest.common.database.withTransaction
import io.github.mrjimin.invest.common.exception.ApiErrorCode
import io.github.mrjimin.invest.common.exception.ApiException
import io.github.mrjimin.invest.common.security.PasswordEncoder
import io.github.mrjimin.invest.domain.user.dto.CreateUserRequest
import io.github.mrjimin.invest.domain.user.model.StudentNumber
import io.github.mrjimin.invest.domain.user.model.User
import io.github.mrjimin.invest.domain.user.model.UserRole
import io.github.mrjimin.invest.domain.user.model.UserType
import io.github.mrjimin.invest.domain.user.model.toStudentNumber
import io.github.mrjimin.invest.domain.user.repository.UserRepository
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    override fun getAllUsers(): List<User> =
        withTransaction {
            userRepository.findAll()
        }

    override fun getUserById(id: Long): User =
        withTransaction {
            userRepository.findById(id)
                ?: throw ApiException(ApiErrorCode.USER_NOT_FOUND)
        }

    override fun getUserByUsername(username: String): User =
        withTransaction {
            userRepository.findByUsername(username.trim())
                ?: throw ApiException(ApiErrorCode.USER_NOT_FOUND)
        }

    override fun registerUser(request: CreateUserRequest): User =
        withTransaction {
            val username = request.username.trim()
            val nickname = request.nickname.trim()

            validateUserInput(
                username = username,
                password = request.password,
                nickname = nickname
            )

            val studentNumber = validateStudentNumber(
                request.type,
                request.studentNumber
            )

            if (userRepository.existsByUsername(username)) {
                throw ApiException(
                    ApiErrorCode.USERNAME_ALREADY_EXISTS
                )
            }

            if (
                studentNumber != null &&
                userRepository.existsByStudentNumber(studentNumber)
            ) {
                throw ApiException(
                    ApiErrorCode.STUDENT_NUMBER_ALREADY_EXISTS
                )
            }

            val now = Clock.System.now()
                .toLocalDateTime(TimeZone.UTC)

            val user = User(
                id = 0L,
                username = username,
                password = passwordEncoder.encode(request.password),
                nickname = nickname,
                type = request.type,
                role = UserRole.USER,
                studentNumber = studentNumber,
                createdAt = now,
                updatedAt = now
            )

            userRepository.save(user)
        }

    override fun deleteUser(id: Long) {
        withTransaction {
            val deleted = userRepository.deleteById(id)

            if (!deleted) {
                throw ApiException(ApiErrorCode.USER_NOT_FOUND)
            }
        }
    }

    private fun validateUserInput(
        username: String,
        password: String,
        nickname: String
    ) {
        if (username.isBlank() || username.length > 50) {
            throw ApiException(
                ApiErrorCode.INVALID_REQUEST,
                "아이디는 1~50자여야 합니다."
            )
        }

        if (password.length !in 8..72) {
            throw ApiException(
                ApiErrorCode.INVALID_REQUEST,
                "비밀번호는 8~72자여야 합니다."
            )
        }

        if (nickname.isBlank() || nickname.length > 50) {
            throw ApiException(
                ApiErrorCode.INVALID_REQUEST,
                "닉네임은 1~50자여야 합니다."
            )
        }
    }

    private fun validateStudentNumber(
        type: UserType,
        value: String?
    ): StudentNumber? {
        if (type == UserType.TEACHER) {
            if (!value.isNullOrBlank()) {
                throw ApiException(
                    ApiErrorCode.INVALID_REQUEST,
                    "교사는 학번을 입력할 수 없습니다."
                )
            }

            return null
        }

        val normalized = value?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: throw ApiException(
                ApiErrorCode.STUDENT_NUMBER_REQUIRED
            )

        return try {
            normalized.toStudentNumber()
        } catch (_: IllegalArgumentException) {
            throw ApiException(
                ApiErrorCode.INVALID_STUDENT_NUMBER
            )
        }
    }
}