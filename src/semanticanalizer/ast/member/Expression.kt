package semanticanalizer.ast.member

import fileWriter
import semanticanalizer.ast.ASTMember
import semanticanalizer.stmember.Method
import symbolTable
import utils.Token
import utils.TokenType
import kotlin.collections.first

val primitiveTypesSet = setOf("int", "boolean", "char")

interface Expression: ASTMember {
    var parentNode: ASTMember
    var type: String?

    fun check(type: String?): String?
    fun generateCodeAsInstanceMetParams()
}

class BinaryExpression(
    override var parentNode: ASTMember,
    var leftExpression: BasicExpression,
    var operator: Token
): Expression {
    lateinit var rightExpression: Expression

    override var type: String? = null

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

        checkCompatibleExpressionTypes(leftType, rightType, operator)

        this.type = resultingPrimitiveType(leftType, operator.lexeme)

        checkCompatibleTypes(type, this.type, operator)

        return this.type!!
    }

    override fun generateCode() {
        leftExpression.generateCode()
        rightExpression.generateCode()

        generateOperatorCode()

    }

    override fun generateCodeAsInstanceMetParams() {
        generateCode()

        fileWriter.writeSwap()
    }

    private fun generateOperatorCode() {
        when (operator.lexeme) {
            "+" -> {
                fileWriter.write("ADD")
            }
            "-" -> {
                fileWriter.write("SUB")
            }
            "*" -> {
                fileWriter.write("MUL")
            }
            "/" -> {
                fileWriter.write("DIV")
            }
            "%" -> {
                fileWriter.write("MOD")
            }
            "&&" -> {
                fileWriter.write("AND")
            }
            "||" -> {
                fileWriter.write("OR")
            }
            "==" -> {
                fileWriter.write("EQ")
            }
            "!=" -> {
                fileWriter.write("NE")
            }
            ">" -> {
                fileWriter.write("GT")
            }
            "<" -> {
                fileWriter.write("LT")
            }
            ">=" -> {
                fileWriter.write("GE")
            }
            "<=" -> {
                fileWriter.write("LE")
            }
        }
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

    override var type: String? = null

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

        this.type = operator?.let {

            if (operand is Primary) {
                var endOfChaining = operand as Primary

                while (endOfChaining.chained != null)
                    endOfChaining = endOfChaining.chained!!

                if (endOfChaining !is VariableAccess && operator!!.lexeme in setOf("++", "--"))
                    throw object: InvalidUnaryOperatorException(operator!!, operandType!!) {
                        override val message: String
                            get() = "Los operadores de incremento y decremento son aplicables solo sobre " +
                                    "accesos a variables"
                    }
            }


            resultingType(operandType, it)
        } ?: operandType

        return this.type
    }

    override fun generateCode() {

        operator?.let {

            val containerBlock = {
                var sentencePointer = parentNode

                while (sentencePointer !is Block) {
                    sentencePointer = when (sentencePointer) {
                        is Expression -> {
                            sentencePointer.parentNode
                        }
                        is Sentence -> {
                            sentencePointer.parentSentence!!
                        }
                        else -> {
                            (sentencePointer as Primary).parent
                        }
                    }
                }

                sentencePointer
            }()

            val containerCallable = containerBlock.parentMember
            val containerClass = containerCallable.parentClass

            var receiverType: String
            val baseAccess = operand as Primary
            var token = baseAccess.token

            if (baseAccess.chained == null) {
                when (token.lexeme) {
                    in containerBlock.visibleVariablesMap -> {
                        val position = -containerBlock.visibleVariablesMap.keys.indexOf(token.lexeme)

                        fileWriter.writeLoad(position)

                        writeUnaryOperator()

                        if (operator!!.lexeme in setOf("++", "--")) {
                            fileWriter.writeStore(position)
                            baseAccess.generateCode()
                        }
                    }

                    in containerCallable.paramMap -> {
                        val stackRecordOffset = (containerCallable as? Method)?.let {
                            if (it.modifier.type == TokenType.STATIC) 2
                            else 3
                        } ?: 3

                        val position = containerCallable.paramMap.keys.indexOf(token.lexeme) + stackRecordOffset + 1

                        fileWriter.writeLoad(position)

                        writeUnaryOperator()

                        if (operator!!.lexeme in setOf("++", "--")) {
                            fileWriter.writeStore(position)
                            baseAccess.generateCode()
                        }
                    }

                    in containerClass.attributeMap -> {
                        val matchingNameAttributeSet = containerClass.attributeMap[token.lexeme]!!

                        //obtener el que sea de esta clase o la última redefinición del atributo del mismo nombre
                        val attribute = matchingNameAttributeSet.firstOrNull {
                            it.parentClass == containerClass
                        } ?: matchingNameAttributeSet.first()

                        val offset = attribute.offsetInCIR

                        fileWriter.writeLoad(3)
                        fileWriter.writeSwap()

                        fileWriter.writeDup()
                        fileWriter.writeLoadRef(offset)

                        writeUnaryOperator()

                        if (operator!!.lexeme in setOf("++", "--")) {
                            fileWriter.writeStoreRef(offset)
                            baseAccess.generateCode()
                        }
                    }
                    else -> {
                        baseAccess.generateCode()
                        writeUnaryOperator()
                    }
                }
            } else {

                //carga normal
                receiverType = baseAccess.generateCodeWithoutChained()

                //iteracion sobre encadenado
                var access = baseAccess.chained!!

                while (access.chained != null) {
                    receiverType = access.generateCodeWithoutChained(receiverType)

                    access = access.chained!!
                }

                token = access.token

                val matchingNameAttributeSet = symbolTable.classMap[receiverType]!!.attributeMap[token.lexeme]!!

                //obtener el que sea de esta clase o la última redefinición del atributo del mismo nombre
                val attribute = matchingNameAttributeSet.first()

                val offset = attribute.offsetInCIR

                fileWriter.writeLoad(3)
                fileWriter.writeSwap()

                fileWriter.writeDup()
                fileWriter.writeLoadRef(offset)

                writeUnaryOperator()

                if (operator!!.lexeme in setOf("++", "--")) {
                    fileWriter.writeStoreRef(offset)
                    baseAccess.generateCode()
                }
            }



        } ?: {
            operand.generateCode()
        }()
    }

    private fun writeUnaryOperator() {
        //te quedaste en determinar qué apilar dependiendo del tipo de operador unario
        //se te ocurrió que capaz se puede factorizar hacia afuera
        when (operator!!.lexeme) {
            "-" -> {
                fileWriter.write("NEG")
            }
            "++" -> {
                fileWriter.writePush("1")
                fileWriter.write("ADD")
            }
            "--" -> {
                fileWriter.writePush("1")
                fileWriter.write("SUB")
            }
            "!" -> {
                fileWriter.write("NOT")
            }
        }
    }

    override fun generateCodeAsInstanceMetParams() {

        generateCode()

        fileWriter.writeSwap()
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