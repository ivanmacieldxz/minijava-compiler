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
    message: String,
    token: Token
) : SemanticException(message, token)

class CircularInheritanceException(
    message: String,
    token: Token
) : SemanticException(message, token)

class BadlyNamedConstructorException(
    message: String,
    token: Token
) : SemanticException(message, token)

class MoreThanOneConstructorDeclarationException(
    message: String,
    token: Token
) : SemanticException(message, token)

class InvalidClassNameException(
    message: String,
    token: Token
) : SemanticException(message, token)

class UndeclaredClassException(
    message: String,
    token: Token
) : SemanticException(message, token)

class InvalidMethodDeclarationException(
    message: String,
    token: Token
) : SemanticException(message, token)

class InvalidInheritanceException(
    message: String,
    token: Token
) : SemanticException(message, token)

class InvalidRedefinitionException(
    message: String,
    token: Token
) : SemanticException(message, token)

class InvalidConstructorDeclarationException(
    message: String,
    token: Token
): SemanticException(message, token)

class InvalidClassDeclarationException(
    message: String,
    token: Token
): SemanticException(message, token)

