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
        TODO("Not yet implemented")
    }

}

interface Operand {

}

class Primitive(
    override var parentNode: ASTMember,
    var token: Token
): Operand, NonSentenceableExpression {

    override fun printItselfAndChildren(nestingLevel: Int) {
        TODO("Not yet implemented")
    }

}

class UnaryOperator(var token: Token) {

}
