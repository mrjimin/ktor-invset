package io.github.mrjimin.invest.domain.user.repository

import io.github.mrjimin.invest.common.database.dbQuery
import io.github.mrjimin.invest.domain.user.model.StudentNumber
import io.github.mrjimin.invest.domain.user.model.User
import io.github.mrjimin.invest.domain.user.model.toStudentNumber
import io.github.mrjimin.invest.domain.user.repository.table.Users
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll

class UserRepositoryImpl : UserRepository {

    override suspend fun findAll(): List<User> = dbQuery {
        Users.selectAll().map { it.toUser() }
    }

    override suspend fun findById(id: Long): User? = dbQuery {
        findByIdInternal(id)
    }

    override suspend fun findByUsername(username: String): User? = dbQuery {
        Users.selectAll()
            .where { Users.username eq username }
            .singleOrNull()
            ?.toUser()
    }

    override suspend fun findByStudentNumber(studentNumber: StudentNumber): User? = dbQuery {
        Users.selectAll()
            .where {
                Users.studentNumber eq studentNumber.value
            }
            .singleOrNull()
            ?.toUser()
    }

    override suspend fun existsByUsername(username: String): Boolean = dbQuery {
        Users.selectAll()
            .where { Users.username eq username }
            .limit(1)
            .any()
    }

    override suspend fun existsByStudentNumber(studentNumber: StudentNumber): Boolean = dbQuery {
        Users.selectAll()
            .where {
                Users.studentNumber eq studentNumber.value
            }
            .limit(1)
            .any()
    }

    override suspend fun save(user: User): User = dbQuery {
        val userId = Users.insertAndGetId {
            it[username] = user.username
            it[password] = user.password
            it[nickname] = user.nickname
            it[type] = user.type
            it[role] = user.role
            it[studentNumber] = user.studentNumber?.value
            it[createdAt] = user.createdAt
            it[updatedAt] = user.updatedAt
        }.value

        findByIdInternal(userId)
            ?: error("생성된 사용자를 조회할 수 없습니다.")
    }

    override suspend fun deleteById(id: Long): Boolean = dbQuery {
        Users.deleteWhere { Users.id eq id } > 0
    }

    private fun findByIdInternal(id: Long): User? =
        Users.selectAll()
            .where { Users.id eq id }
            .singleOrNull()
            ?.toUser()

    private fun ResultRow.toUser() = User(
        id = this[Users.id].value,
        username = this[Users.username],
        password = this[Users.password],
        nickname = this[Users.nickname],
        type = this[Users.type],
        role = this[Users.role],
        studentNumber = this[Users.studentNumber]?.toStudentNumber(),
        createdAt = this[Users.createdAt],
        updatedAt = this[Users.updatedAt]
    )
}