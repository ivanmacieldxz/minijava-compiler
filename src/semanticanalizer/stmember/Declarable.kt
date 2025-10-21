package semanticanalizer.stmember

import semanticanalizer.ast.member.Block
import utils.Token
import utils.Token.DummyToken

interface Declarable {

    var token: Token
    var declarationCompleted: Boolean

    fun isWellDeclared()
    fun isDummyClass() = this == DummyClass
    fun isDummyContext() = this == DummyContext

}

object DummyContext: Declarable {
    override var token: Token = DummyToken
    override var declarationCompleted = true

    override fun isWellDeclared() {}
}

interface Modifiable: Declarable {
    var modifier: Token
}

interface ClassMember: Declarable {
    val parentClass: Class
}

interface Callable: Declarable{
    var paramMap: MutableMap<String, FormalArgument>
    var block: Block?

    fun printBlock(nestingLevel: Int) {
        block?.printItselfAndChildren(nestingLevel)
    }
}

interface Typed {
    var typeToken: Token
}