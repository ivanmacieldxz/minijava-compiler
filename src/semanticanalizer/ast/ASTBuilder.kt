package semanticanalizer.ast

import semanticanalizer.ast.member.Block
import semanticanalizer.ast.member.CompoundSentence
import semanticanalizer.ast.member.If
import symbolTable
import utils.Token

class ASTBuilder {

    var currentContext: ASTMember? = null
    lateinit var metVarName: Token
    val blockStack = ArrayDeque<Block>()
    val ifWithoutElseStack = ArrayDeque<If>()

    fun currentBlockIfs(): Set<If> {
        val compoundSentencesList = blockStack.first().childrenList.filter { it is CompoundSentence } as List<CompoundSentence>
        val compoundSentenceStack = ArrayDeque(compoundSentencesList)
        val resultingIfSet = mutableSetOf<If>()

        var currentCompoundSentence: CompoundSentence? = compoundSentenceStack.removeFirstOrNull()

        while (currentCompoundSentence != null) {
            if (currentCompoundSentence is If) {
                resultingIfSet.add(currentCompoundSentence)
            }

            if (currentCompoundSentence.body is CompoundSentence)
                compoundSentenceStack.addLast(currentCompoundSentence.body as CompoundSentence)

            currentCompoundSentence = compoundSentenceStack.removeFirstOrNull()
        }

        return resultingIfSet
    }
}

