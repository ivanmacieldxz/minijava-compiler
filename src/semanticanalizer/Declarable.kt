package semanticanalizer

import utils.Token
import utils.Token.DummyToken

interface Declarable {

    var token: Token
    var parent: Token

    abstract fun isWellDeclared()
    fun isDummyClass() = this == DummyClass
    fun isDummyContext() = this == DummyContext

}

object DummyContext: Declarable {
    override var parent: Token = DummyToken
    override var token: Token = DummyToken

    override fun isWellDeclared() {}
}

object DummyClass: Class() {}

abstract class Modifiable(): Declarable {
    var modifier: Token = DummyToken
}

open class Class(override var token: Token = DummyToken, override var parent: Token = DummyToken) : Modifiable() {

    var constructor: Constructor? = null
    var parentClass: Token = DummyToken
    var methodMap = mutableMapOf<String, Method>()


    val attributeMap = mutableMapOf<String, Attribute>()

    override fun isWellDeclared() {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return "[${modifier?.toString()?.plus(" ") ?: ""}class ${token!!.lexeme}${if (parentClass != null) " extends $parentClass" else ""}]"
    }
}

interface ClassMember {
    val parentClass: Class
}

class Method(
    override var token: Token = DummyToken,
    override var parent: Token = DummyToken,
    override val parentClass: Class
) : Modifiable(), ClassMember {

    override fun isWellDeclared() {
        TODO("Not yet implemented")
    }
}

class Constructor(
    override var token: Token = DummyToken,
    override var parent: Token = DummyToken,
    override val parentClass: Class
) : Modifiable(), ClassMember {

    var paramMap = mutableMapOf<String, Token>()

    override fun isWellDeclared() {
        TODO("Not yet implemented")
    }
}

class Attribute(
    override var token: Token = DummyToken,
    override var parent: Token = DummyToken,
    override val parentClass: Class
) : Declarable, ClassMember {

    override fun isWellDeclared() {
        TODO("Not yet implemented")
    }
}