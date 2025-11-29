package semanticanalizer.ast.member

import semanticanalizer.SemanticException
import utils.Token

open class InvalidUnaryOperatorException(
    token: Token,
    type: String
): SemanticException("El operador ${token.lexeme} es inválido para el tipo $type", token)

class VarDeclarationAsSingleSentenceException(
    token: Token,
    parentToken: Token
): SemanticException("No se admiten declaraciones de variables como única sentencia dentro de un ${parentToken.lexeme}", token)

class AssignmentAsConditionException(
    token: Token,
    parentToken: Token
): SemanticException("No se admiten asignaciones como condiciones de ${parentToken.lexeme}", token)

class AssignmentAsReturnExpressionException(
    token: Token,
    parentToken: Token
): SemanticException("No se admiten asignaciones como condiciones de ${parentToken.lexeme}", token)

class AssignmentAsActualArgumentException(
    token: Token
): SemanticException("No se admiten asignaciones como parámetros actuales", token)

class NonApplicableBinaryOperatorException(
    token: Token
): SemanticException("El operador $token no es aplicable sobre los operandos provistos.", token)

class RepeatedVariableDeclarationException(
    token: Token
): SemanticException("El nombre $token ya es utilizado en el contexto actual.", token)

class NonExistentClassException(
    token: Token,
    message: String
): SemanticException(message, token)

class ExpressionInvalidAsSingleSentenceException(
    token: Token
): SemanticException("Solo se admiten llamadas a métodos, incrementos de variables, o asignaciones como sentencias únicas en este contexto.", token)

class InvalidChainingException(
    token: Token
): SemanticException("No se puede acceder a un miembro de un tipo primitivo o void.", token)

class TypeMismatchException(
    token: Token,
    actualTokenType: String?,
    expectedType: String
): SemanticException("El tipo $actualTokenType del operando no coincide con el tipo $expectedType esperado", token)

class UnexpectedNullOperandException(
    token: Token,
    expectedType: String
): SemanticException("Se esperaba un operando de tipo $expectedType, pero se encontró null.", token)

class InvalidCallException(
    token: Token,
    message: String
): SemanticException(message, token)

class InvalidVarAccessException(
    token: Token
): SemanticException("No se puede resolver el símbolo $token.", token)

class InvalidAttributeAccessException(
    token: Token,
    receiverClass: String
): SemanticException("La clase $receiverClass no tiene un atributo $token", token)

class InvalidReturnException(
    token: Token,
    message: String
): SemanticException(message, token)

class InvalidVarInitializationException(
    token: Token,
    message: String
): SemanticException(message, token)

class InvalidAssignmentException(
    token: Token,
    message: String
): SemanticException(message, token)

class AccessToInstanceMemberFromStaticContextException(
    token: Token,
    message: String
): SemanticException(message, token)