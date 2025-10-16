package semanticanalizer

import utils.Token
import utils.Token.DummyToken


class SymbolTable {

    companion object Predefined {
        val classesNames = setOf("String", "Object", "System")
    }

    val classMap = mutableMapOf<String, Class>(
        "Object" to Object,
        "System" to System,
        "String" to StringClass
    )
    var currentClass: Class = DummyClass
    var currentContext: Declarable = DummyContext
    val accumulator = Accumulator()

    class Accumulator {
        var className: Token = DummyToken
        var classParent: Token = Object.token
        var foundInheritance = false

        var methodName: Token = DummyToken
        var attrName: Token = DummyToken

        var memberType: Token = DummyToken

        var params = mutableMapOf<String, FormalArgument>()
        var modifier: Token = DummyToken

        var expectedClosingBrackets = 0

        fun clear() {
            className = DummyToken
            classParent = Object.token
            foundInheritance = false

            methodName = DummyToken
            memberType = DummyToken

            attrName = DummyToken

            params = mutableMapOf()
            modifier = DummyToken
        }
    }

    fun checkDeclarations() {
        classMap.values.forEach {
            it.isWellDeclared()
        }
    }

    fun consolidate() {
        classMap.values.forEach {
            it.takeIf { it.isConsolidated.not() }?.consolidate()
        }
    }

}