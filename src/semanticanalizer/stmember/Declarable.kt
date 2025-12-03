package semanticanalizer.stmember

import semanticanalizer.ast.member.Block
import utils.Token
import utils.Token.DummyToken

interface Declarable {

    var token: Token
    var declarationCompleted: Boolean

    fun isWellDeclared(){}
    fun isDummyClass() = this == DummyClass
    fun isDummyContext() = this == DummyContext

    fun generateCode(){}

}

object DummyContext: Declarable {
    override var token: Token = DummyToken
    override var declarationCompleted = true
}

interface Modifiable: Declarable {
    var modifier: Token
}

interface ClassMember: Declarable {
    val parentClass: Class
}

interface Callable: Declarable, ClassMember {
    var paramMap: MutableMap<String, FormalArgument>
    var block: Block?

    fun printBlock(nestingLevel: Int) {
        block?.printItselfAndChildren(nestingLevel)
    }

    fun printSubAST() {
        block?.printSubAST(2)
    }

    fun getCodeLabel() = "${token.lexeme}@${parentClass.token.lexeme}"
}

interface Typed {
    var typeToken: Token
}