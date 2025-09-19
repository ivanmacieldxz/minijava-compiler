package parser

import utils.Token
import utils.TokenType

abstract class SyntacticException(
    val token: Token,
    val expected: TokenType
): Exception() {
    override fun toString(): String {
        return "Error sintáctico en la línea ${token.lineNumber}: se esperaba $expected, pero se encontró ${token.type}." +
                "\n[Error:${token.lexeme}|${token.lineNumber}]"
    }
}

/**
 * Should be thrown when expecting a token (terminal) and the current token is not what was expected
 */
class MismatchException(
    token: Token,
    expected: TokenType
): SyntacticException(token, expected) {

}

/**
 * Should be thrown when expecting a non-terminal and the current token is not in that NTs firsts
 */
class NotInFirstsException(
    token: Token,
    expected: TokenType
): SyntacticException(token, expected) {

}