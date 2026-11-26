package io.github.mrjimin.invest.domain.user.repository

import io.github.mrjimin.invest.domain.user.model.StudentNumber
import io.github.mrjimin.invest.domain.user.model.User

interface UserRepository {
    fun findAll(): List<User>
    fun findById(id: Long): User?
    fun findByUsername(username: String): User?
    fun findByStudentNumber(studentNumber: StudentNumber): User?

    fun existsByUsername(username: String): Boolean
    fun existsByStudentNumber(studentNumber: StudentNumber): Boolean

    fun save(user: User): User

    fun deleteById(id: Long): Boolean
}