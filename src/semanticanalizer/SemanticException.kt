package semanticanalizer

import utils.Token

abstract class SemanticException(
    override val message: String,
    val token: Token
) : Exception(message) {
    override fun toString(): String {
        return "Error Semántico en la línea ${token.lineNumber}: $message\n\n" +
                "[Error:${token.lexeme}|${token.lineNumber}]"
    }
}