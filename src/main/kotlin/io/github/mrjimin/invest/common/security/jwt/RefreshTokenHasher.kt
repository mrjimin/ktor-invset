package io.github.mrjimin.invest.common.security.jwt

import java.security.MessageDigest

object RefreshTokenHasher {

    fun hash(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(token.toByteArray(Charsets.UTF_8))

        return hash.joinToString("") { byte ->
            "%02x".format(byte)
        }
    }
}