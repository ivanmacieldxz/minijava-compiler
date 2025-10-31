package semanticanalizer.ast.member

import semanticanalizer.ast.ASTMember
import utils.Token

interface Expression: ASTMember {
    var parentNode: ASTMember
}

class Assignment(
    override var parentNode: ASTMember,
    var leftExpression: Expression,
    var token: Token
): Expression {

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


}

class BinaryExpression(
    override var parentNode: ASTMember,
    var leftExpression: BasicExpression,
    var operator: BinaryOperator? = null
): Expression {
    lateinit var rightExpression: Expression

    override fun printItselfAndChildren(nestingLevel: Int) {
        print("\t".repeat(nestingLevel) + leftExpression + operator)
        rightExpression.printItselfAndChildren(0)
    }

    override fun toString(): String {
        return "$leftExpression$operator$rightExpression"
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Exp binaria ($operator):")
        leftExpression.printSubAST(nestingLevel + 1)
        rightExpression.printSubAST(nestingLevel + 1)
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

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Exp básica:")
        operand.printSubAST(nestingLevel + 1)
    }

}
