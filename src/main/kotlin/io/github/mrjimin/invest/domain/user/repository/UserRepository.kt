package io.github.mrjimin.invest.domain.user.repository

import io.github.mrjimin.invest.domain.user.model.StudentNumber
import io.github.mrjimin.invest.domain.user.model.User

interface UserRepository {
    suspend fun findAll(): List<User>
    suspend fun findById(id: Long): User?
    suspend fun findByUsername(username: String): User?
    suspend fun findByStudentNumber(studentNumber: StudentNumber): User?

    suspend fun existsByUsername(username: String): Boolean
    suspend fun existsByStudentNumber(studentNumber: StudentNumber): Boolean

    suspend fun save(user: User): User

    suspend fun deleteById(id: Long): Boolean
}