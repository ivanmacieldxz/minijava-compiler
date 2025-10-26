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
    override fun printItselfAndChildren(nestingLevel: Int) {
        print(this)
    }
}

class ParenthesizedExpression(var parentExpression: Expression): Primary {
    var expression: Expression? = null


    override fun toString(): String {
        return "($expression)"
    }
}

class LiteralPrimary(override var token: Token): Primary, TokenizedOperand {
    override fun toString():String {
        return token.lexeme
    }
}

class VariableAccess(
    override var token: Token
): Primary, TokenizedOperand {

}

class MethodAccess(
    override var token: Token
): Primary, TokenizedOperand {

}

class ConstructorCall(
    override var token: Token
): Primary, TokenizedOperand {
    var arguments = mutableListOf<Expression>()

    override fun toString(): String {
        return "new $token()"
    }
}

class StaticMethodCall(): Primary {
    var calledClass: Token? = null
    var calledMethod: Token? = null
}