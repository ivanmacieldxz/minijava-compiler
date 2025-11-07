package semanticanalizer.ast.member

import semanticanalizer.ast.ASTMember
import semanticanalizer.stmember.Callable
import semanticanalizer.stmember.Constructor
import semanticanalizer.stmember.Method
import utils.Token

interface Sentence: ASTMember {

    var parentMember: Callable
    var token: Token
    var parentSentence: Sentence?

    fun check()

}

class Block(
    override var parentMember: Callable,
    override var token: Token,
    override var parentSentence: Sentence? = null
): Sentence {

    var childrenList = mutableListOf<ASTMember>()
    var visibleVariablesMap = mutableMapOf<String, LocalVar>()

    fun insertVarDeclaration(localVar: LocalVar) {
        if (localVar.varName.lexeme in visibleVariablesMap)
            throw Exception("Una variable del mismo nombre fue declarada anteriormente")
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
                    checkIsCall(it)
                    it.check(null)
                }
                is BinaryExpression -> throw Exception()
            }
        }
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
        println("\t".repeat(nestingLevel) + "If:")
        condition?.printSubAST(nestingLevel + 1)
        body?.printSubAST(nestingLevel)
        elseSentence?.printSubAST(nestingLevel)
    }

    override fun check() {

        condition!!.check("boolean")

        when (val body = body) {
            is Sentence -> {
                if (body is LocalVar)
                    throw Exception("No se admiten declaraciones de variables como única sentencia de un if")

                body.check()
            }
            is Expression -> {
                if (body is BasicExpression)
                    checkIsCall(body)

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
        println("\t".repeat(nestingLevel) + "Else:")
        body?.printSubAST(nestingLevel)
    }

    override fun check() {
        when (val body = body) {
            is Sentence -> {
                if (body is LocalVar)
                    throw Exception("No se admiten declaraciones de variables como única sentencia de un else")
                body.check()
            }
            is Expression -> {
                if (body is BasicExpression)
                    checkIsCall(body)

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

    override fun check() {
        condition!!.check("boolean")

        when (val body = body) {
            is Sentence -> {
                if (body is LocalVar)
                    throw Exception("No se admiten declaraciones de variables como única sentencia de un else")
                body.check()
            }
            is Expression -> {
                if (body is BasicExpression)
                    checkIsCall(body)

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
                    throw Exception("Se esperaba que devolviera un valor")
                else if (body != null && parent.typeToken.lexeme == "void")
                    throw Exception("No se puede devolver un valor en un método void")
                else if (body != null && parent.typeToken.lexeme != "void")
                    (body as Expression).check(parent.typeToken.lexeme)
            }
            is Constructor -> {
                if (body != null)
                    throw Exception("No se admite el uso de return con un valor dentro de un constructor")
            }
        }
    }
}

class LocalVar(
    override var parentMember: Callable,
    override var token: Token,
    override var parentSentence: Sentence?,
    var containerBlock: Block
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
        if (varName.lexeme in parentMember.paramMap || varName.lexeme in containerBlock.visibleVariablesMap)
            throw Exception("Un parámetro o variable del mismo nombre ya es visible en este contexto")

        type = expression.check(null)
            ?: throw Exception("Una variable local no puede ser inicializada con null")

        if (type == "void")
            throw Exception("La expresión asignada no devuelve un valor")
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

    override fun check() {
        if (leftExpression is BinaryExpression)
            throw Exception("El lado izquierdo de la asignación no es asignable")

        checkLeftSideAssignable()

        rightExpression.check(
            leftExpression.check(null)
        )
    }

    private fun checkLeftSideAssignable() {
        val operand = (leftExpression as BasicExpression).operand

        if (operand is Primitive)
            throw Exception("El lado izquierdo de la asignación no es asignable")

        var chained = (operand as Primary).chained

        if (chained == null && operand !is VariableAccess)
            throw Exception("El lado izquierdo de la asignación no es asignable")

        while (chained?.chained != null) {
            chained = chained.chained
        }

        if (chained != null && chained !is VariableAccess)
            throw Exception("El lado izquierdo de la asignación no es asignable")
    }

}

private fun checkIsCall(basicExpression: BasicExpression) {
    val operand = basicExpression.operand

    if (operand is Primitive)
        throw Exception("Solo se admiten llamadas a métodos como sentencias únicas en este contexto.")

    var chained = (operand as Primary).chained

    if (chained == null && operand !is Call)
        throw Exception("Solo se admiten llamadas a métodos como sentencias únicas en este contexto")

    while (chained?.chained != null) {
        chained = chained.chained
    }

    if (chained != null && chained !is Call)
        throw Exception("Solo se admiten llamadas a métodos como sentencias únicas en este contexto")

}