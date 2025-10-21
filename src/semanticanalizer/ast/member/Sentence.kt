package semanticanalizer.ast.member

import semanticanalizer.ast.ASTContext
import semanticanalizer.stmember.Callable

interface Sentence: ASTContext {

    var parentMember: Callable
    var parentSentence: Sentence?

    fun printItselfAndChildren(nestingLevel: Int)

}

class Block(
    override var parentMember: Callable,
    override var parentSentence: Sentence? = null
): Sentence {

    var childSentencesList = mutableListOf<Sentence>()

    override fun printItselfAndChildren(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "{")
        childSentencesList.forEach {
            it.printItselfAndChildren(nestingLevel + 1)
        }
        println("\t".repeat(nestingLevel) + "}")
    }
}

interface CompoundSentence: Sentence {
    var body: Sentence?
}

class If(override var parentMember: Callable, override var parentSentence: Sentence?): CompoundSentence {
    var condition: Expression? = null
    override var body: Sentence? = null
    var elseSentence: Else? = null

    override fun printItselfAndChildren(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "if (<expresion>):")
        body?.printItselfAndChildren(nestingLevel + 1)
        elseSentence?.printItselfAndChildren(nestingLevel)
    }
}

class Else(override var parentMember: Callable, override var parentSentence: Sentence?): CompoundSentence {

    override var body: Sentence? = null

    override fun printItselfAndChildren(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "else: ")
        body?.printItselfAndChildren(nestingLevel + 1)
    }
}

class While(override var parentMember: Callable, override var parentSentence: Sentence?): CompoundSentence {

    var condition: Expression? = null
    override var body: Sentence? = null

    override fun printItselfAndChildren(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "while (<expresion>):")
        body?.printItselfAndChildren(nestingLevel + 1)
    }
}

class Return(override var parentMember: Callable, override var parentSentence: Sentence?): CompoundSentence {
    override var body: Sentence? = null

    override fun printItselfAndChildren(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "return <expresion>;")
    }
}