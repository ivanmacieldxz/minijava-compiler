package semanticanalizer.stmember

import semanticanalizer.SemanticException
import utils.Token

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

