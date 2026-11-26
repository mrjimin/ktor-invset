
package io.github.mrjimin.invest.common.security.role

import io.github.mrjimin.invest.common.security.jwt.JwtPrincipal
import io.github.mrjimin.invest.domain.user.model.UserRole

fun JwtPrincipal.hasRole(role: UserRole): Boolean =
    this.role == role

fun JwtPrincipal.hasAnyRole(vararg roles: UserRole): Boolean =
    this.role in roles