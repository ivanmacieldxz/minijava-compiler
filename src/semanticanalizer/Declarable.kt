package semanticanalizer

import utils.Token
import utils.Token.DummyToken
import java.util.Collections.emptyMap

interface Declarable {

    var token: Token
    var parent: Token

    fun isWellDeclared()
    fun isDummyClass() = this == DummyClass
    fun isDummyContext() = this == DummyContext

}

object DummyContext: Declarable {
    override var parent: Token = DummyToken
    override var token: Token = DummyToken

    override fun isWellDeclared() {}
}

interface Modifiable: Declarable {
    var modifier: Token
}

interface ClassMember {
    val parentClass: Class
}

interface Callable {
    var paramMap: MutableMap<String, Token>
}

interface Typed {
    var type: Token
}

object DummyClass: Class() {}

open class Class() : Modifiable {

    override var token: Token = DummyToken
    var constructor: Constructor? = null
    override var parent: Token = DummyToken
        set(_) {println("Boludo")}

    var parentClass: Token = DummyToken
    var methodMap = mutableMapOf<String, Method>()
    override var modifier: Token = DummyToken

    val attributeMap = mutableMapOf<String, Attribute>()

    override fun isWellDeclared() {
        TODO("Not yet implemented")
    }
    override fun toString(): String {
        return "[${modifier.lexeme} class ${token.lexeme} extends ${parentClass.lexeme}]"
    }
}

class Constructor(
    override var token: Token = DummyToken,
    override val parentClass: Class
) : Declarable, ClassMember, Callable {
    override var parent: Token = parentClass.token

    override var paramMap: MutableMap<String, Token> = emptyMap()
    override fun isWellDeclared() {
        TODO("Not yet implemented")
    }

}

class Method(
    override var token: Token = DummyToken,
    override var parentClass: Class
) : Modifiable, ClassMember, Callable, Typed {

    override var parent: Token = parentClass.token
    override var paramMap: MutableMap<String, Token> = emptyMap()
    override var modifier: Token = DummyToken
    override var type: Token = DummyToken

    override fun isWellDeclared() {
        TODO("Not yet implemented")
    }
}

class Attribute(
    override var token: Token = DummyToken,
    override val parentClass: Class
) : Declarable, ClassMember, Typed {

    override var parent: Token = parentClass.token
    override var type: Token = DummyToken

    override fun isWellDeclared() {
        TODO("Not yet implemented")
    }
}