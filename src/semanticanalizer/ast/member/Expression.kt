package semanticanalizer.ast.member

import semanticanalizer.ast.ASTMember
import utils.Token

val primitiveTypesSet = setOf("int", "boolean", "char", "String")

interface Expression: ASTMember {
    var parentNode: ASTMember

    fun check(type: String?): String?
}

class BinaryExpression(
    override var parentNode: ASTMember,
    var leftExpression: BasicExpression,
    var operator: Token
): Expression {
    lateinit var rightExpression: Expression

    companion object {
        private val logicOperators = setOf("||", "&&")
        private val arithmeticOperators = setOf("+", "-", "*", "/", "%")
        private val comparisonOperators = setOf("==", "!=", "<", ">", ">=", "<=")
        private val intOperators = comparisonOperators + arithmeticOperators
        private val booleanOperators = logicOperators + setOf("==" + "!=")
    }

    override fun printItselfAndChildren(nestingLevel: Int) {
        print("\t".repeat(nestingLevel) + leftExpression + operator)
        rightExpression.printItselfAndChildren(0)
    }

    override fun toString(): String {
        return "$leftExpression$operator$rightExpression"
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Exp binaria ($operator):")
        leftExpression.printSubAST(nestingLevel + 1)
        rightExpression.printSubAST(nestingLevel + 1)
    }

    override fun check(type: String?): String {
        val leftType = leftExpression.check(type)

        if (leftType !in primitiveTypesSet)
            throw Exception("No se pueden realizar operaciones binarias con tipos no primitivos")

        when (leftType) {
            "int" -> {
                if (operator.lexeme !in intOperators)
                    throw Exception("El operador de la expresión no es compatible con el operando de la izquierda")
            }
            "boolean" -> {
                if (operator.lexeme !in booleanOperators)
                    throw Exception("El operador de la expresión no es compatible con el operando de la izquierda")
            }
            "char", "String" -> {
                if (operator.lexeme !in logicOperators)
                    throw Exception("El operador de la expresión no es compatible con el operando de la izquierda")
            }
            null -> throw Exception("El operador no es aplicable a null")
        }

        rightExpression.check(leftType)

        return resultingPrimitiveType(leftType, operator.lexeme)
    }

    private fun resultingPrimitiveType(type: String, operator: String) =
        when (type) {
            "boolean" -> "boolean"
            "int" -> {
                if (operator in arithmeticOperators)
                    "int"
                else
                    "boolean"
            }
            else -> {
                "boolean"
            }
        }

}

class BasicExpression(
    override var parentNode: ASTMember,
): Expression {
    var operator: Token? = null
    lateinit var operand: Operand

    override fun printItselfAndChildren(nestingLevel: Int) {
        print("\t".repeat(nestingLevel) + this)
    }

    override fun toString(): String {
        return (operator?.toString()?:"") + operand
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Exp básica:")
        operator?.let { println("\t".repeat(nestingLevel) + "Operador unario: $operator") }
        operand.printSubAST(nestingLevel + 1)
    }

    override fun check(type: String?): String? {
        val operandType = operand.check(type)

        return operator?.let {
            resultingType(operandType, it.lexeme)
        } ?: operandType
    }

    private fun resultingType(operandType: String?, operator: String) =
        when (operandType) {
            "int" -> {
                if (operator == "!")
                    throw Exception("Operador inválido para el operando")
                else
                    operandType
            }
            "boolean" -> {
                if (operator != "!")
                    throw Exception("Operador inválido para el operando")
                else
                    operandType
            }
            else -> throw Exception("Operador inválido para el operando")
        }

}
