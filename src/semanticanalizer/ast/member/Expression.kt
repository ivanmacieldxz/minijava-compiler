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

    override fun generateCode() {
        TODO("Not yet implemented")
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

        checkCompatibleExpressionTypes(leftType, rightType, operator)

        val expType = resultingPrimitiveType(leftType, operator.lexeme)

        checkCompatibleTypes(type, expType, operator)

        return expType
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
    override var parentNode: ASTMember
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

    override fun generateCode() {
        //TODO: considerar el oeprador para la generación de código
        operand.generateCode()
    }

    override fun check(type: String?): String? {
        val operandType = operand.check(type)

        return operator?.let {

            if (operand is Primary) {
                var endOfChaining = operand as Primary

                while (endOfChaining.chained != null)
                    endOfChaining = endOfChaining.chained!!

                if (endOfChaining !is VariableAccess)
                    throw object: InvalidUnaryOperatorException(operator!!, operandType!!) {
                        override val message: String
                            get() = "Los operadores de incremento y decremento son aplicables solo sobre " +
                                    "accesos a variables"
                    }
            }


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

fun checkCompatibleExpressionTypes(leftType: String?, rightType: String?, token: Token) {
    if (leftType == null && rightType in primitiveTypesSet || leftType in primitiveTypesSet && rightType == null)
        throw UnexpectedNullOperandException(token, (leftType ?: rightType)!!)

    if (leftType in primitiveTypesSet) {
        if (rightType !in primitiveTypesSet)
            throw TypeMismatchException(token, rightType, leftType!!)

        if (rightType != leftType)
            throw TypeMismatchException(token, rightType, leftType!!)
    } else {
        if (rightType in primitiveTypesSet)
            throw TypeMismatchException(token, rightType, leftType!!)
    }
}