package io.github.mrjimin.invest.domain.user.repository.table

import io.github.mrjimin.invest.domain.user.model.UserRole
import io.github.mrjimin.invest.domain.user.model.UserType
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.datetime

object Users : LongIdTable("users") {
    val username = varchar("username", 50).uniqueIndex()
    val password = varchar("password", 255)
    val nickname = varchar("nickname", 50)
    val type = enumerationByName<UserType>("type", 20)
    val role = enumerationByName<UserRole>("role", 20).default(UserRole.USER)
    val studentNumber = varchar("student_number", 5).nullable().uniqueIndex()
    val createdAt = datetime("created_at")
    val updatedAt = datetime("updated_at")
}