package semanticanalizer.ast.member

import fileWriter
import semanticanalizer.ast.ASTMember
import semanticanalizer.stmember.Callable
import semanticanalizer.stmember.Constructor
import semanticanalizer.stmember.Method
import symbolTable
import utils.Token
import utils.TokenType
import kotlin.collections.contains
import kotlin.collections.first
import kotlin.collections.firstOrNull
import kotlin.collections.indexOf

interface Sentence: ASTMember {

    var parentMember: Callable
    var token: Token
    var parentSentence: Sentence?

    fun check()

}

open class Block(
    override var parentMember: Callable,
    override var token: Token,
    override var parentSentence: Sentence? = null
): Sentence {

    var childrenList = mutableListOf<ASTMember>()
    var visibleVariablesMap = mutableMapOf<String, LocalVar>()

    fun insertVarDeclaration(localVar: LocalVar) {
        if (localVar.varName.lexeme in visibleVariablesMap || localVar.varName.lexeme in parentMember.paramMap)
            throw RepeatedVariableDeclarationException(localVar.varName)
        else {
            visibleVariablesMap[localVar.varName.lexeme] = localVar
        }
    }

    override fun printItselfAndChildren(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "{")
        childrenList.forEach {
            it.printItselfAndChildren(nestingLevel + 1)
            if (it is Expression)
                println()
        }
        println("\t".repeat(nestingLevel) + "}")
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Bloque:")
        childrenList.forEach { children ->
            children.printSubAST(nestingLevel + 1)
        }
    }

    override fun check() {
        childrenList.forEach {
            when (it) {
                is Sentence -> {
                    it.check()
                }
                is BasicExpression -> {
                    checkIsValidAsSingleSentence(it)
                    it.check(null)
                }
                is BinaryExpression -> throw ExpressionInvalidAsSingleSentenceException(
                    it.leftExpression.operand.token
                )
            }
        }
    }

    override fun generateCode() {
        visibleVariablesMap.size.takeIf { it != 0 }?.let {
            fileWriter.writeRMEM(visibleVariablesMap.filter {
                it.value.parentSentence == this
            }.size)
        }

        childrenList.forEach {
            it.generateCode()

            if (it is Expression && it.type != "void")
                fileWriter.writePop()
        }

        fileWriter.writeFreeLocalVars(visibleVariablesMap.filter {
            it.value.parentSentence == this
        }.size)
    }
}

interface CompoundSentence: Sentence {
    var body: ASTMember?
}

class If(
    override var parentMember: Callable,
    override var token: Token,
    override var parentSentence: Sentence?
): CompoundSentence {
    var condition: Expression? = null
    override var body: ASTMember? = null
    var elseSentence: Else? = null

    override fun printItselfAndChildren(nestingLevel: Int) {
        print("\t".repeat(nestingLevel) + "if (")
        condition?.printItselfAndChildren(0)
        println("):")
        body?.printItselfAndChildren(nestingLevel + 1)
        if (body is Expression)
            println()
        elseSentence?.printItselfAndChildren(nestingLevel)
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "If (id ${System.identityHashCode(this)}):")
        condition?.printSubAST(nestingLevel + 1)
        body?.printSubAST(nestingLevel + 1)
        elseSentence?.printSubAST(nestingLevel)
    }

    override fun generateCode() {
        condition!!.generateCode()

        val ifIdentityHashCode = System.identityHashCode(this)
        val endThenLabel = "endThen@$ifIdentityHashCode"
        val endIfLabel = "endIf@$ifIdentityHashCode"

        fileWriter.write("BF $endThenLabel")
        body!!.generateCode()
        fileWriter.writeLabeledInstruction(endThenLabel, "NOP")

        elseSentence?.let {
            fileWriter.write("JUMP $endIfLabel")
            it.generateCode()
            fileWriter.writeLabeledInstruction(endIfLabel, "NOP")
        }

    }

    override fun check() {

        condition!!.check("boolean")

        when (val body = body) {
            is Sentence -> {
                if (body is LocalVar)
                    throw VarDeclarationAsSingleSentenceException(body.token, token)

                body.check()
            }
            is Expression -> {
                if (body is BasicExpression)
                    checkIsValidAsSingleSentence(body)

                body.check(null)
            }
        }
    }
}

class Else(
    override var parentMember: Callable,
    override var token: Token,
    override var parentSentence: Sentence?
): CompoundSentence {

    override var body: ASTMember? = null

    override fun printItselfAndChildren(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "else: ")
        body?.printItselfAndChildren(nestingLevel + 1)
        if (body is Expression)
            println()
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Else (padre: ${parentSentence!!.javaClass} id ${System.identityHashCode(parentSentence)}):")
        body?.printSubAST(nestingLevel + 1)
    }

    override fun generateCode() {
        body!!.generateCode()
    }

    override fun check() {
        when (val body = body) {
            is Sentence -> {
                if (body is LocalVar)
                    throw VarDeclarationAsSingleSentenceException(body.token, token)
                body.check()
            }
            is Expression -> {
                if (body is BasicExpression)
                    checkIsValidAsSingleSentence(body)

                body.check(null)
            }
        }
    }
}

class While(
    override var parentMember: Callable,
    override var token: Token,
    override var parentSentence: Sentence?
): CompoundSentence {

    var condition: Expression? = null
    override var body: ASTMember? = null

    override fun printItselfAndChildren(nestingLevel: Int) {
        print("\t".repeat(nestingLevel) + "while (")
        condition?.printItselfAndChildren(0)
        println("):")
        body?.printItselfAndChildren(nestingLevel)
        if (body is Expression)
            println()
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "While:")
        condition?.printSubAST(nestingLevel + 1)
        body?.printSubAST(nestingLevel + 1)
    }

    override fun generateCode() {
        val whileIdentityHashCode = System.identityHashCode(this)
        val startLabel = "whileStart@$whileIdentityHashCode"
        val endLabel = "endWhile@$whileIdentityHashCode"

        fileWriter.writeLabeledInstruction(startLabel, "NOP")
        condition!!.generateCode()
        fileWriter.write("BF $endLabel")
        body!!.generateCode()
        fileWriter.write("JUMP $startLabel")
        fileWriter.writeLabeledInstruction(endLabel, "NOP")

    }

    override fun check() {
        condition!!.check("boolean")

        when (val body = body) {
            is Sentence -> {
                if (body is LocalVar)
                    throw VarDeclarationAsSingleSentenceException(body.token, token)
                body.check()
            }
            is Expression -> {
                if (body is BasicExpression)
                    checkIsValidAsSingleSentence(body)

                body.check(null)
            }
        }
    }
}

class Return(
    override var parentMember: Callable,
    override var token: Token,
    override var parentSentence: Sentence?
): Sentence {
    var body: Expression? = null

    override fun printItselfAndChildren(nestingLevel: Int) {
        print("\t".repeat(nestingLevel) + "return ")
        body?.printItselfAndChildren(0)
        println()
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Return:")
        body?.printSubAST(nestingLevel + 1)
    }

    override fun check() {
        when (val parent = parentMember) {
            is Method -> {
                if (body == null && parent.typeToken.lexeme != "void")
                    throw InvalidReturnException(token, "Se esperaba que el método devolviera un valor.")
                else if (body != null && parent.typeToken.lexeme == "void")
                    throw InvalidReturnException(token, "Se esperaba que el método no devolviera un valor.")
                else if (body != null && parent.typeToken.lexeme != "void") {
                    val typeOfReturn = (body as Expression).check(null)

                    checkCompatibleTypes(parent.typeToken.lexeme, typeOfReturn, token)
                }
            }
            is Constructor -> {
                if (body != null)
                    throw InvalidReturnException(
                        token,
                        "No se admite el uso de return con un valor dentro de un constructor"
                    )
            }
        }
    }

    override fun generateCode() {
        fileWriter.writeFreeLocalVars(parentMember.block!!.visibleVariablesMap.size)

        body?.let{
            it.generateCode()

            if (parentMember is Constructor)
                fileWriter.writeStore(2 + parentMember.paramMap.size + 2)
            else if ((parentMember as Method).typeToken.type != TokenType.VOID) {
                val storeSize = 2 + parentMember.paramMap.size + (2.takeIf {
                    (parentMember as Method).modifier.type != TokenType.STATIC
                } ?: 1)

                fileWriter.writeStore(storeSize)
            }
        }

        val returnSize = parentMember.paramMap.size +
                (1.takeIf { parentMember is Constructor ||
                        (parentMember as Method).modifier.type != TokenType.STATIC }
                ?: 0)

        fileWriter.writeStoreFP()
        fileWriter.writeRet(returnSize)

    }
}

class LocalVar(
    override var parentMember: Callable,
    override var token: Token,
    override var parentSentence: Sentence?
): Sentence {
    lateinit var type: String
    lateinit var varName: Token
    lateinit var expression: Expression

    override fun printItselfAndChildren(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "var $varName = $expression")

    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Var local($varName):")
        expression.printSubAST(nestingLevel + 1)
    }

    override fun check() {
        type = expression.check(null)
            ?: throw InvalidVarInitializationException(
                token,
                "No se admite la inicialización de variables con null"
            )

        if (type == "void")
            throw InvalidVarInitializationException(
                token,
                "La expresión del lado derecho de la declaración no devuelve un valor."
            )
    }

    override fun generateCode() {
        var ownerBlock: Block?
        var sentencePointer = parentSentence!!

        while (sentencePointer !is Block) {
            sentencePointer = sentencePointer.parentSentence!!
        }

        ownerBlock = sentencePointer

        expression.generateCode()

        val index = -ownerBlock.visibleVariablesMap.values.indexOf(this)

        fileWriter.writeStore(index)

    }

}

class Assignment(
    override var token: Token,
    override var parentMember: Callable,
    override var parentSentence: Sentence?,
    var leftExpression: Expression
): Sentence {

    lateinit var rightExpression: Expression

    override fun printItselfAndChildren(nestingLevel: Int) {
        leftExpression.printItselfAndChildren(nestingLevel)
        print(" = ")
        rightExpression.printItselfAndChildren(0)
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Asignacion:")
        leftExpression.printSubAST(nestingLevel + 1)
        rightExpression.printSubAST(nestingLevel + 1)
    }

    override fun generateCode() {

        val containerBlock = {
            var sentencePointer = parentSentence!!

            while (sentencePointer !is Block) {
                sentencePointer = sentencePointer.parentSentence!!
            }

            sentencePointer
        }()

        val containerCallable = containerBlock.parentMember
        val containerClass = containerCallable.parentClass

        rightExpression.generateCode()

        var receiverType: String
        val baseAccess = (leftExpression as BasicExpression).operand as? VariableAccess ?:
            (leftExpression as BasicExpression).operand as LiteralPrimary
        var token = baseAccess.token

        if (baseAccess.chained == null) {
            when (token.lexeme) {
                in containerBlock.visibleVariablesMap -> {
                    val position = -containerBlock.visibleVariablesMap.keys.indexOf(token.lexeme)

                    fileWriter.writeStore(position)
                }

                in containerCallable.paramMap -> {
                    val stackRecordOffset = (containerCallable as? Method)?.let {
                        if (it.modifier.type == TokenType.STATIC) 2
                        else 3
                    } ?: 3

                    val position = containerCallable.paramMap.keys.indexOf(token.lexeme) + stackRecordOffset + 1

                    fileWriter.writeStore(position)
                }

                else -> {
                    val matchingNameAttributeSet = containerClass.attributeMap[token.lexeme]!!

                    //obtener el que sea de esta clase o la última redefinición del atributo del mismo nombre
                    val attribute = matchingNameAttributeSet.firstOrNull {
                        it.parentClass == containerClass
                    } ?: matchingNameAttributeSet.first()

                    val offset = attribute.offsetInCIR

                    fileWriter.writeLoad(3)
                    fileWriter.writeSwap()
                    fileWriter.writeStoreRef(offset)
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

            fileWriter.writeSwap()
            fileWriter.writeStoreRef(offset)
        }




    }

    override fun check() {
        checkLeftSideAssignable()

        val leftType = leftExpression.check(null)
        val rightType = rightExpression.check(null)

        checkCompatibleTypes(leftType, rightType, token)
    }

    private fun checkLeftSideAssignable() {
        if (leftExpression is BinaryExpression)
            throw InvalidAssignmentException(
                token,
                "El lado izquierdo de la asignación no es asignable"
            )

        val operand = (leftExpression as BasicExpression).operand

        if (operand is Primitive)
            throw InvalidAssignmentException(
                token,
                "El lado izquierdo de la asignación no es asignable"
            )

        var chained = (operand as Primary).chained

        if (chained == null && operand !is VariableAccess)
            throw InvalidAssignmentException(
                token,
                "El lado izquierdo de la asignación no es asignable"
            )

        while (chained?.chained != null) {
            chained = chained.chained
        }

        if (chained != null && chained !is VariableAccess)
            throw InvalidAssignmentException(
                token,
                "El lado izquierdo de la asignación no es asignable"
            )
    }

}

private fun checkIsValidAsSingleSentence(basicExpression: BasicExpression) {
    val operand = basicExpression.operand
    val operator = basicExpression.operator?.type

    if (operand is Primitive)
        throw ExpressionInvalidAsSingleSentenceException(basicExpression.operator ?: operand.token)

    var chained = (operand as Primary).chained

    if (chained == null &&
        (operand is ParenthesizedExpression ||
                operand is VariableAccess && operator != TokenType.DECREMENT && operator != TokenType.INCREMENT))
        throw ExpressionInvalidAsSingleSentenceException(basicExpression.operator ?: operand.token)

    while (chained?.chained != null) {
        chained = chained.chained
    }

    if (chained != null) {
        if (chained !is Call && chained is VariableAccess
            && operator != TokenType.DECREMENT && operator != TokenType.INCREMENT)
        throw ExpressionInvalidAsSingleSentenceException(basicExpression.operator ?: operand.token)
    } else if (operand !is Call && (operand !is VariableAccess || operator != TokenType.DECREMENT && operator != TokenType.INCREMENT))
        throw ExpressionInvalidAsSingleSentenceException(basicExpression.operator ?: operand.token)

}