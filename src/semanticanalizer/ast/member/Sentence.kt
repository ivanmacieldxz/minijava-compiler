package semanticanalizer.ast.member

import semanticanalizer.ast.ASTContext
import semanticanalizer.stmember.Callable

interface Sentence: ASTContext {

    var parentMember: Callable
    var parentSentence: Sentence?

    fun printItselfAndChildren(nestingLevel: Int);

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

class If(override var parentMember: Callable, override var parentSentence: Sentence?): Sentence {
    var condition: Expression? = null
    var thenSentence: Sentence? = null
    var elseSentence: Sentence? = null

    override fun printItselfAndChildren(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "if (<expresion>)")
        thenSentence?.printItselfAndChildren(nestingLevel + 1)
    }
}

