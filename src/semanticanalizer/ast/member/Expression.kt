package semanticanalizer.ast.member

import semanticanalizer.ast.ASTMember
import utils.Token

val primitiveTypesSet = setOf("int", "boolean", "char")

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
        private val equalityComparisonOperators = setOf("==", "!=")
        private val comparisonOperators = setOf("<", ">", ">=", "<=")
        private val intOperators = equalityComparisonOperators + comparisonOperators + arithmeticOperators
        private val booleanOperators = logicOperators + equalityComparisonOperators
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
        val leftType = leftExpression.check(null)

        when (leftType) {
            "int" -> {
                if (operator.lexeme !in intOperators)
                    throw NonApplicableBinaryOperatorException(operator)
            }
            "boolean" -> {
                if (operator.lexeme !in booleanOperators)
                    throw NonApplicableBinaryOperatorException(operator)
            }
            else -> {
                if (operator.lexeme !in equalityComparisonOperators)
                    throw NonApplicableBinaryOperatorException(operator)
            }
        }

        val rightType = rightExpression.check(null)

        checkCompatibleTypes(leftType, rightType, operator)

        return resultingPrimitiveType(leftType, operator.lexeme)
    }

    private fun resultingPrimitiveType(left: String?, operator: String) =
        when (left) {
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
            resultingType(operandType, it)
        } ?: operandType
    }

    private fun resultingType(operandType: String?, operator: Token) =
        when (operandType) {
            "int" -> {
                if (operator.lexeme == "!")
                    throw InvalidUnaryOperatorException(operator, operandType)
                else
                    operandType
            }
            "boolean" -> {
                if (operator.lexeme != "!")
                    throw InvalidUnaryOperatorException(operator, operandType)
                else
                    operandType
            }
            else -> throw InvalidUnaryOperatorException(operator, operandType.toString())
        }

}

