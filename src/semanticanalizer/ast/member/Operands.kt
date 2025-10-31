package semanticanalizer.ast.member

import semanticanalizer.ast.ASTMember
import utils.Token

interface Operand: ASTMember {}

interface TokenizedOperand: Operand {
    var token: Token
}

interface Call {
    var arguments: MutableList<Expression>
}

class Primitive(
    override var token: Token
): TokenizedOperand {

    override fun toString():String {
        return token.lexeme
    }

    override fun printItselfAndChildren(nestingLevel: Int) {
        TODO("Not yet implemented")
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Primitivo ($token)")
    }
}

interface Primary : Operand {
    var chained: Primary?
    var parent: ASTMember

    override fun printItselfAndChildren(nestingLevel: Int) {
        print("\t".repeat(nestingLevel) + this)
    }
}

class ParenthesizedExpression(override var parent: ASTMember): Primary {
    var expression: Expression? = null
    override var chained: Primary? = null

    override fun toString(): String {
        return "($expression${chained?.let { ").$it" } ?: ")"}"
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Expresion parentizada:")
        expression?.printSubAST(nestingLevel + 1)
        chained?.let {
            println("\t".repeat(nestingLevel + 1) + "Encadenado:")
            it.printSubAST(nestingLevel + 1)
        }
    }
}

class LiteralPrimary(
    override var token: Token,
    override var parent: ASTMember
): Primary, TokenizedOperand {
    override var chained: Primary? = null

    override fun toString():String {
        return "$token${chained?.let { ".$it" } ?: ""}"
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Primario literal ($token):")
        chained?.let {
            println("\t".repeat(nestingLevel + 1) + "Encadenado:")
            it.printSubAST(nestingLevel + 1)
        }
    }
}

class VariableAccess(
    override var token: Token,
    override var parent: ASTMember
): Primary, TokenizedOperand {
    override var chained: Primary? = null

    override fun toString(): String {
        return "$token${chained?.let { ".$it" } ?: ""}"
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Acceso var ($token):")
        chained?.let {
            println("\t".repeat(nestingLevel + 1) + "Encadenado:")
            it.printSubAST(nestingLevel + 1)
        }
    }
}

class MethodCall(
    override var token: Token,
    override var parent: ASTMember
): Primary, TokenizedOperand, Call {
    override var chained: Primary? = null
    override lateinit var arguments: MutableList<Expression>

    override fun toString(): String {
        return "$token(${arguments})${chained?.let { ".$it" } ?: ""}"
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Llamada a método ($token):")
        println("\t".repeat(nestingLevel + 1) + "Argumentos:")
        arguments.forEach {
            it.printSubAST(nestingLevel + 1)
        }
        chained?.let {
            println("\t".repeat(nestingLevel + 1) + "Encadenado:")
            it.printSubAST(nestingLevel + 1)
        }
    }
}

class ConstructorCall(
    override var token: Token,
    override var parent: ASTMember
): Primary, TokenizedOperand, Call {
    override lateinit var arguments: MutableList<Expression>
    override var chained: Primary? = null

    override fun toString(): String {
        return "new $token(${arguments})${chained?.let { ".$it" } ?: ""}"
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Llamada a constructor ($token):")
        println("\t".repeat(nestingLevel + 1) + "Argumentos:")
        arguments.forEach {
            it.printSubAST(nestingLevel + 1)
        }
        chained?.let {
            println("\t".repeat(nestingLevel + 1) + "Encadenado:")
            it.printSubAST(nestingLevel + 1)
        }
    }
}

class StaticMethodCall(
    override var parent: ASTMember,
    var calledClass: Token,
    var calledMethod: Token
): Primary, Call {

    override lateinit var arguments: MutableList<Expression>
    override var chained: Primary? = null

    override fun toString():String {
        return "$calledClass.$calledMethod(${arguments})${chained?.let { ".$it" } ?: ""}"
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Llamada a método estático ($calledClass.$calledMethod):")
        println("\t".repeat(nestingLevel + 1) + "Argumentos:")
        arguments.forEach {
            it.printSubAST(nestingLevel + 1)
        }
        chained?.let {
            println("\t".repeat(nestingLevel + 1) + "Encadenado:")
            it.printSubAST(nestingLevel + 1)
        }
    }
}