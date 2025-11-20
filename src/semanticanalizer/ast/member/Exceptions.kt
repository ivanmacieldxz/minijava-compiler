package semanticanalizer.ast.member

import semanticanalizer.SemanticException
import utils.Token

class InvalidUnaryOperatorException(
    token: Token,
    type: String
): SemanticException("El operador ${token.lexeme} es inválido para el tipo $type", token)

class VarDeclarationAsOnlySentenceException(
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
    token: Token
): SemanticException("", token)

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

