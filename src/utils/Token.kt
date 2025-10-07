package utils

open class Token (
    val type: TokenType,
    val lexeme: String,
    var lineNumber: Int
) {
    fun isDummyToken() = this == DummyToken

    override fun toString(): String {
        return lexeme
    }

    object EOFToken: Token (
        TokenType.EOF,
        "",
        -1
    )

    object DummyToken: Token (
        TokenType.DOT,
        "",
        -1
    ) {
        override fun toString(): String {
            return "PRINTING-DUMMY-TOKEN"
        }
    }

}

