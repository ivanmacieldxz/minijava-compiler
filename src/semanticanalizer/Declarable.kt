package semanticanalizer

import symbolTable
import utils.Token
import utils.Token.DummyToken
import java.util.Collections.emptyMap
import java.util.Stack

interface Declarable {

    var token: Token

    fun isWellDeclared()
    fun isDummyClass() = this == DummyClass
    fun isDummyContext() = this == DummyContext

}

object DummyContext: Declarable {
    override var token: Token = DummyToken

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
}

interface Typed {
    var type: Token
}

object DummyClass: Class() {}

open class Class() : Modifiable {

    private companion object {
        var withoutCircularity = mutableSetOf<Class>()
        var mightOrDoHaveCircularity = mutableSetOf<Class>()
    }

    override var token: Token = DummyToken
    var constructor: Constructor = Constructor(parentClass = this)

    var parentClass: Token = DummyToken
    var methodMap = mutableMapOf<String, Method>()
    val attributeMap = mutableMapOf<String, Attribute>()

    override var modifier: Token = DummyToken

    override fun isWellDeclared() {
        checkCircularInheritance()

//        attributeMap.values.forEach {
//            it.isWellDeclared()
//        }
//
//        methodMap.values.forEach {
//            it.isWellDeclared()
//        }
    }

    private fun checkCircularInheritance() {
        //recordá que usaste una pila en lugar de una única var por si
        //llegás a implementar el logro de interfaces, que permiten herencia múltiple
        //cambiaría muy poco en tal caso (tendrías que guardar el camino inválido
        //para no invalidar ramas válidas de herencia
        val border = Stack<Class>()

        border.push(this)

        while (border.isNotEmpty()) {
            val current = border.pop()

            if (current in withoutCircularity) {
                break
            }

            if (current in mightOrDoHaveCircularity)
                throw CircularInheritanceException("Herencia circular detectada")

            mightOrDoHaveCircularity.add(current)

            if (current.hasExplicitParent()) {
                val parentClass = symbolTable.classMap[current.parentClass.lexeme]
                    ?: throw UndeclaredClassException("La clase padre no está declarada")

                border.push(parentClass)
            }

        }

        withoutCircularity.addAll(mightOrDoHaveCircularity)
        mightOrDoHaveCircularity.removeAll { true }
    }

    private fun hasExplicitParent() = parentClass.isDummyToken().not()


    override fun toString(): String {
        var strRep = "class ${token.lexeme} "

        if (modifier != DummyToken)
            strRep = modifier.lexeme + " " + strRep

        if (parentClass != DummyToken)
            strRep += "extends ${parentClass.lexeme}\n"

        if (constructor.isDefaultConstructor().not())
            strRep += "Constructor: $constructor"

        strRep += "Métodos: $methodMap\nAtributos: $attributeMap\n"

        return strRep
    }
}

class Constructor(
    override var token: Token = DummyToken,
    override val parentClass: Class
) : ClassMember, Callable {

    override var paramMap: MutableMap<String, FormalArgument> = emptyMap()

    override fun toString(): String {
        return paramMap.toString() + "\n"
    }

    override fun isWellDeclared() {
        TODO("Not yet implemented")
    }

    fun isDefaultConstructor(): Boolean {
        return token == DummyToken
    }

}

class Method(
    override var token: Token = DummyToken,
    override var parentClass: Class
) : Modifiable, ClassMember, Callable, Typed {

    override var paramMap: MutableMap<String, FormalArgument> = emptyMap()
    override var modifier: Token = DummyToken
    override var type: Token = DummyToken

    override fun toString(): String {
        return "$modifier\n $type $token $paramMap"
    }

    override fun isWellDeclared() {
        TODO("Not yet implemented")
    }
}

class Attribute(
    override var token: Token = DummyToken,
    override val parentClass: Class
) : Declarable, ClassMember, Typed {

    override var type: Token = DummyToken

    override fun toString(): String {
        return "$type $token"
    }

    override fun isWellDeclared() {
        TODO("Not yet implemented")
    }
}

class FormalArgument(
    override var token: Token = DummyToken,
    override var type: Token = DummyToken,
    var member: Callable
): Declarable, Typed {

    override fun toString(): String {
        return "Param: ${type.lexeme} ${token.lexeme}"
    }

    override fun isWellDeclared() {
        TODO("Not yet implemented")
    }

}