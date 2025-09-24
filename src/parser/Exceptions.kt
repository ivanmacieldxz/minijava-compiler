package parser

import utils.SyntacticStackable
import utils.Token

abstract class SyntacticException(
    val token: Token,
    val expected: Set<SyntacticStackable>
): Exception() {
    override fun toString(): String {
        return "Error sintáctico en la línea ${token.lineNumber}: se esperaba alguno de los siguientes:" +
                " ${expected.joinToString(" | ")}, pero se encontró: ${token.type}." +
                "\n[Error:${token.lexeme}|${token.lineNumber}]"
    }
}

/**
 * Should be thrown when expecting a token (terminal) and the current token is not what was expected
 */
class MismatchException(
    token: Token,
    expected: Set<SyntacticStackable>
): SyntacticException(token, expected)


/**
 * Should be thrown when expecting a non-terminal and the current token is not in that NTs firsts
 */
class UnexpectedTerminalException(
    token: Token,
    expected: Set<SyntacticStackable>
): SyntacticException(token, expected)

