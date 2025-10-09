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

class RepeatedDeclarationException(
    override val message: String,
    token: Token
) : SemanticException(message, token)

class CircularInheritanceException(
    override val message: String,
    token: Token
) : SemanticException(message, token)

class BadlyNamedConstructorException(
    override val message: String,
    token: Token
) : SemanticException(message, token)

class MoreThanOneConstructorDeclarationException(
    override val message: String,
    token: Token
) : SemanticException(message, token)

class InvalidClassNameException(
    override val message: String,
    token: Token
) : SemanticException(message, token)

class UndeclaredClassException(
    override val message: String,
    token: Token
) : SemanticException(message, token)

class InvalidMethodDeclarationException(
    override val message: String,
    token: Token
) : SemanticException(message, token)

class InvalidInheritanceException(
    override val message: String,
    token: Token
) : SemanticException(message, token)

class InvalidRedefinitionException(
    override val message: String,
    token: Token
) : SemanticException(message, token)
