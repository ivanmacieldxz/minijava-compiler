package semanticanalizer.ast.member

import fileWriter
import semanticanalizer.ast.ASTMember
import semanticanalizer.stmember.Callable
import semanticanalizer.stmember.Constructor
import semanticanalizer.stmember.Method
import utils.Token
import utils.TokenType

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
            fileWriter.writeRMEM(visibleVariablesMap.size)
        }

        childrenList.forEach {
            it.generateCode()
            //TODO: manejar la liberación de espacio cuando la sentencia sea una expresión
            // en los casos que corresponda
        }

        fileWriter.writeFreeLocalVars(visibleVariablesMap.size)
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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

    override fun generateCode() {
        TODO("Not yet implemented")
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

    override fun generateCode() {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
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