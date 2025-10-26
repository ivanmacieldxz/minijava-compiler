package semanticanalizer.ast.member

import semanticanalizer.ast.ASTMember
import utils.Token

interface Expression: ASTMember {
    var parentNode: ASTMember
}

class Assignment(
    override var parentNode: ASTMember,
    var token: Token
): Expression {
    lateinit var leftExpression: Expression
    lateinit var rightExpression: Expression

    override fun printItselfAndChildren(nestingLevel: Int) {
        TODO("Not yet implemented")
    }
}

class BinaryExpression(
    override var parentNode: ASTMember,
    var leftExpression: BasicExpression,
    var operator: BinaryOperator? = null
): Expression {
    lateinit var rightExpression: Expression

    override fun printItselfAndChildren(nestingLevel: Int) {
        print("" + leftExpression + operator)
        rightExpression.printItselfAndChildren(0)
    }

}

class BasicExpression(
    override var parentNode: ASTMember,
): Expression {
    var operator: UnaryOperator? = null
    lateinit var operand: Operand

    override fun printItselfAndChildren(nestingLevel: Int) {
        print("\t".repeat(nestingLevel) + this)
    }

    override fun toString(): String {
        return (operator?.token?.toString()?:"") + operand
    }

}
