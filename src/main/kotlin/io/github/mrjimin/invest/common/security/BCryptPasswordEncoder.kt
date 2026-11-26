package io.github.mrjimin.invest.common.security

import at.favre.lib.crypto.bcrypt.BCrypt

class BCryptPasswordEncoder(
    private val cost: Int = DEFAULT_COST
) : PasswordEncoder {

    companion object {
        const val DEFAULT_COST = 12
    }

    override fun encode(rawPassword: String): String =
        BCrypt.withDefaults().hashToString(cost, rawPassword.toCharArray())

    override fun matches(rawPassword: String, encodedPassword: String): Boolean =
        BCrypt.verifyer().verify(rawPassword.toCharArray(), encodedPassword)
            .verified
}