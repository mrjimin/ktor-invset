package io.github.mrjimin.invest.domain.user.model

@JvmInline
value class StudentNumber(val value: String) {

    init {
        require(value.isNotBlank())
        require(REGEX.matches(value))
    }

    companion object {
        private val REGEX = Regex("^\\d{5}$")

        fun of(value: String) = StudentNumber(value.trim())
    }

    override fun toString(): String = "StudentNumber($value)"
}

fun String.toStudentNumber() = StudentNumber.of(this)
