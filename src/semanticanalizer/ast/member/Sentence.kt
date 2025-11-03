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
                is BasicExpression -> {
                    if (it.operand !is Call || it.operand is ConstructorCall)
                        throw Exception("Solo se admiten llamadas a métodos como expresiones dentro de un bloque")
                }
            }
            when (it) {
                is Sentence -> {
                    it.check()
                }
                is Expression -> {
                    it.check(null)
                }
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
                    if (body.operand !is Call || body.operand is ConstructorCall)
                        throw Exception("Solo se admiten llamadas a métodos como expresiones dentro de un bloque")

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
                    if (body.operand !is Call || body.operand is ConstructorCall)
                        throw Exception("Solo se admiten llamadas a métodos como expresiones dentro de un bloque")

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
                    if (body.operand !is Call || body.operand is ConstructorCall)
                        throw Exception("Solo se admiten llamadas a métodos como expresiones dentro de un bloque")

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
                if (body is Assignment)
                    throw Exception("No se admiten asignaciones como expresiones de retorno")

                (body as Expression).check(parent.typeToken.lexeme)
            }
            is Constructor -> {
                throw Exception("No se admite el uso de return dentro de un constructor")
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

    override fun check() {
        //TODO: chequear que el nombre no esté ya en uso

        type = expression.check(null)
            ?: throw Exception("Una variable local no puede ser inicializada con null")

        if (type == "void")
            throw Exception("La expresión asignada no devuelve un valor")
    }

}