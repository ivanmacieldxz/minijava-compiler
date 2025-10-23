package semanticanalizer.ast.member

import semanticanalizer.ast.ASTMember
import semanticanalizer.stmember.Callable
import utils.Token

interface Sentence: ASTMember {

    var parentMember: Callable
    var token: Token
    var parentSentence: Sentence?

}

class Block(
    override var parentMember: Callable,
    override var token: Token,
    override var parentSentence: Sentence? = null
): Sentence {

    var childrenList = mutableListOf<ASTMember>()

    override fun printItselfAndChildren(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "{")
        childrenList.forEach {
            it.printItselfAndChildren(nestingLevel + 1)
        }
        println("\t".repeat(nestingLevel) + "}")
    }
}

interface CompoundSentence: Sentence {
    var body: Sentence?
}

class If(
    override var parentMember: Callable,
    override var token: Token,
    override var parentSentence: Sentence?
): CompoundSentence {
    var condition: Expression? = null
    override var body: Sentence? = null
    var elseSentence: Else? = null

    override fun printItselfAndChildren(nestingLevel: Int) {
        print("\t".repeat(nestingLevel) + "if (")
        condition?.printItselfAndChildren(0)
        println("):")
        body?.printItselfAndChildren(nestingLevel + 1)
        elseSentence?.printItselfAndChildren(nestingLevel)
    }
}

class Else(
    override var parentMember: Callable,
    override var token: Token,
    override var parentSentence: Sentence?
): CompoundSentence {

    override var body: Sentence? = null

    override fun printItselfAndChildren(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "else: ")
        body?.printItselfAndChildren(nestingLevel + 1)
    }
}

class While(
    override var parentMember: Callable,
    override var token: Token,
    override var parentSentence: Sentence?
): CompoundSentence {

    var condition: Expression? = null
    override var body: Sentence? = null

    override fun printItselfAndChildren(nestingLevel: Int) {
        print("\t".repeat(nestingLevel) + "while (")
        condition?.printItselfAndChildren(0)
        println("):")
        body?.printItselfAndChildren(nestingLevel + 1)
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
        println("")
    }
}