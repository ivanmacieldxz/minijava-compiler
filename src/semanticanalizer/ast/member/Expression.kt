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
            "char", "String" -> {
                if (operator.lexeme !in logicOperators)
                    throw NonApplicableBinaryOperatorException(operator)
            }
            null -> throw NonApplicableBinaryOperatorException(operator)
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

class EmptyExpression(
    override var parentNode: ASTMember
): Expression {

    override fun check(type: String?): String? {
        return null
    }

    override fun printItselfAndChildren(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Empty Expression")
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Empty Expression")
    }

}