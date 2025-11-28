package semanticanalizer.ast

interface ASTMember {
    fun printItselfAndChildren(nestingLevel: Int)
    fun printSubAST(nestingLevel: Int)
}