package utils

open class Token (
    val type: TokenType,
    val lexeme: String,
    var lineNumber: Int
) {
    object EOFToken: Token (
        TokenType.EOF,
        "",
        -1
    )

    override fun toString(): String {
        return lexeme
    }
}

