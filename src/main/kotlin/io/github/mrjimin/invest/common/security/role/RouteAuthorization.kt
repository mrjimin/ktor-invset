package io.github.mrjimin.invest.common.security.role

import io.github.mrjimin.invest.common.exception.ApiErrorCode
import io.github.mrjimin.invest.common.exception.ApiException
import io.github.mrjimin.invest.common.extension.jwtPrincipal
import io.github.mrjimin.invest.common.security.jwt.JwtPrincipal
import io.github.mrjimin.invest.domain.user.model.UserRole
import io.github.mrjimin.invest.plugin.JWT_AUTH
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

private class RoleAuthorizationConfig {
    var roles: Set<UserRole> = emptySet()
}

private class AuthorizedRouteSelector(
    private val roles: Set<UserRole>
) : RouteSelector() {

    override suspend fun evaluate(
        context: RoutingResolveContext,
        segmentIndex: Int
    ): RouteSelectorEvaluation =
        RouteSelectorEvaluation.Constant

    override fun toString(): String =
        "(authorize ${roles.joinToString(",")})"
}

private val RoleAuthorizationPlugin = createRouteScopedPlugin(
    name = "RoleAuthorization",
    createConfiguration = ::RoleAuthorizationConfig
) {
    val roles = pluginConfig.roles

    on(AuthenticationChecked) { call ->
        val principal = call.jwtPrincipal()
        if (!principal.hasAnyRole(*roles.toTypedArray())) {
            throw ApiException(ApiErrorCode.FORBIDDEN)
        }
    }
}

fun Route.authenticated(
    build: Route.() -> Unit
) {
    authenticate(JWT_AUTH) {
        build()
    }
}

fun Route.authorized(
    vararg roles: UserRole,
    build: Route.() -> Unit
) {
    require(roles.isNotEmpty()) {
        "At least one role must be specified."
    }

    authenticate(JWT_AUTH) {
        createChild(AuthorizedRouteSelector(roles.toSet())).apply {
            install(RoleAuthorizationPlugin) {
                this.roles = roles.toSet()
            }

            build()
        }
    }
}
