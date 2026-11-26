package io.github.mrjimin.invest.domain.user.service

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

    override suspend fun getAllUsers(): List<User> =
        userRepository.findAll()

    override suspend fun getUserById(id: Long): User =
        userRepository.findById(id)
            ?: throw ApiException(ApiErrorCode.USER_NOT_FOUND)

    override suspend fun getUserByUsername(username: String): User =
        userRepository.findByUsername(username)
            ?: throw ApiException(ApiErrorCode.USER_NOT_FOUND)

    override suspend fun registerUser(request: CreateUserRequest): User {
        val username = request.username.trim()
        val nickname = request.nickname.trim()

        validateUserInput(username, request.password, nickname)
        checkUsernameDuplicate(username)

        val studentNumber = validateStudentNumber(request.type, request.studentNumber)
        if (studentNumber != null) {
            checkStudentNumberDuplicate(studentNumber)
        }

        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)

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

        return userRepository.save(user)
    }

    override suspend fun deleteUser(id: Long) {
        val deleted = userRepository.deleteById(id)

        if (!deleted) {
            throw ApiException(ApiErrorCode.USER_NOT_FOUND)
        }
    }

    private fun validateUserInput(
        username: String,
        password: String,
        nickname: String
    ) {
        if (username.isBlank() || username.length > 50) {
            throw ApiException(ApiErrorCode.INVALID_REQUEST)
        }

        if (password.length !in 8..72) {
            throw ApiException(ApiErrorCode.INVALID_REQUEST)
        }

        if (nickname.isBlank() || nickname.length > 50) {
            throw ApiException(ApiErrorCode.INVALID_REQUEST)
        }
    }

    private fun validateStudentNumber(
        type: UserType,
        value: String?
    ): StudentNumber? {
        if (type == UserType.TEACHER) {
            if (!value.isNullOrBlank()) {
                throw ApiException(ApiErrorCode.INVALID_REQUEST)
            }

            return null
        }

        val studentNumber = try {
            value?.trim()
                ?.takeIf { it.isNotBlank() }
                ?.toStudentNumber()
                ?: throw ApiException(ApiErrorCode.STUDENT_NUMBER_REQUIRED)
        } catch (_: IllegalArgumentException) {
            throw ApiException(
                ApiErrorCode.INVALID_STUDENT_NUMBER
            )
        }

        return studentNumber
    }

    private suspend fun checkUsernameDuplicate(username: String) {
        if (userRepository.existsByUsername(username)) {
            throw ApiException(ApiErrorCode.USERNAME_ALREADY_EXISTS)
        }
    }

    private suspend fun checkStudentNumberDuplicate(studentNumber: StudentNumber) {
        if (userRepository.existsByStudentNumber(studentNumber)) {
            throw ApiException(ApiErrorCode.STUDENT_NUMBER_ALREADY_EXISTS)
        }
    }
}