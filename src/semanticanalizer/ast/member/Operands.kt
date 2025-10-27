package semanticanalizer.ast.member

import semanticanalizer.ast.ASTMember
import utils.Token

interface Operand: ASTMember {}

interface TokenizedOperand: Operand {
    var token: Token
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
        return "($expression${chained?.let { ".$it" } ?: ""})"
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
}

class VariableAccess(
    override var token: Token,
    override var parent: ASTMember
): Primary, TokenizedOperand {
    override var chained: Primary? = null

    override fun toString(): String {
        return "$token${chained?.let { ".$it" } ?: ""}"
    }
}

class MethodCall(
    override var token: Token,
    override var parent: ASTMember
): Primary, TokenizedOperand {
    override var chained: Primary? = null
    lateinit var arguments: MutableList<Expression>

    override fun toString(): String {
        return "$token()${chained?.let { ".$it" } ?: ""}"
    }
}

class ConstructorCall(
    override var token: Token,
    override var parent: ASTMember
): Primary, TokenizedOperand {
    var arguments = mutableListOf<Expression>()
    override var chained: Primary? = null

    override fun toString(): String {
        return "new $token()${chained?.let { ".$it" } ?: ""}"
    }
}

class StaticMethodCall(
    override var parent: ASTMember,
    var calledClass: Token,
    var calledMethod: Token
): Primary {

    lateinit var arguments: MutableList<Expression>
    override var chained: Primary? = null

    override fun toString():String {
        return "$calledClass.$calledMethod(${""/*arguments*/})${chained?.let { ".$it" } ?: ""}"
    }
}