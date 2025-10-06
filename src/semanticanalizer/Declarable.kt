package semanticanalizer

import utils.Token

interface Declarable {

    var token: Token?
    var parent: Declarable?

    fun isWellDeclared()

}

object DummyContext: Declarable {
    override var token: Token? = null
    override var parent: Declarable? = null
    override fun isWellDeclared() {}
}

interface Modifiable: Declarable {
    var modifier: Token?
}

open class Class: Modifiable {

    override var token: Token? = null
    override var parent: Declarable? = null
    override var modifier: Token? = null

    var constructor: Constructor? = null
    var parentClass: Token? = null
    var methodMap = mutableMapOf<String, Method>()
    val attributeMap = mutableMapOf<String, Attribute>()


    override fun isWellDeclared() {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return "[${modifier?.toString()?.plus(" ") ?: ""}class ${token!!.lexeme}${if (parentClass != null) " extends $parentClass" else ""}]"
    }

}

object DummyClass: Class()

class Method: Modifiable {

    override var token: Token? = null
    override var modifier: Token? = null
    override var parent: Declarable? = null

    override fun isWellDeclared() {
        TODO("Not yet implemented")
    }
}

class Constructor: Declarable {

    override var token: Token? = null
    override var parent: Declarable? = null

    override fun isWellDeclared() {
        TODO("Not yet implemented")
    }
}

class Attribute: Declarable {

    override var token: Token? = null
    override var parent: Declarable? = null

    override fun isWellDeclared() {
        TODO("Not yet implemented")
    }
}