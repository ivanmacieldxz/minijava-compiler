package lexer.token

data class Token (
    val type: TokenType,
    val lexeme: String,
    val lineNumber: Int
)