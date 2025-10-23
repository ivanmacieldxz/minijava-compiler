package semanticanalizer.ast.member

import semanticanalizer.ast.ASTMember
import utils.Token

interface Expression: ASTMember {
    var parentNode: ASTMember
}

interface NonSentenceableExpression: Expression {}

class Assignment(
    override var parentNode: ASTMember,
    var token: Token
): Expression {
    override fun printItselfAndChildren(nestingLevel: Int) {
        TODO("Not yet implemented")
    }

}

class BasicExpression(
    override var parentNode: ASTMember,
): Expression {
    var operator: UnaryOperator? = null
    lateinit var operand: Operand

    override fun printItselfAndChildren(nestingLevel: Int) {
        print((operator?.token?.toString()?:"")+operand.token)
    }

}

interface Operand {
    var token: Token
}

class Primitive(
    override var parentNode: ASTMember,
    override var token: Token
): Operand, NonSentenceableExpression {

    override fun printItselfAndChildren(nestingLevel: Int) {}

}

class UnaryOperator(var token: Token) {

}
