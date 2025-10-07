package semanticanalizer

import utils.Token
import utils.Token.DummyToken


class SymbolTable {

    val classMap = mutableMapOf<String, Class>()
    var currentClass: Class = DummyClass
    var currentContext: Declarable = DummyContext
    val accumulator = Accumulator()

    class Accumulator {
        var className: Token = DummyToken
        var classParent: Token = DummyToken
        var foundInheritance = false

        var methodName: Token = DummyToken
        var methodType: Token = DummyToken

        var attrName: Token = DummyToken
        var attrType: Token = DummyToken

        var params = mutableMapOf<String, Token>()
        var modifier: Token = DummyToken

        fun clear() {
            className = DummyToken
            classParent = DummyToken
            foundInheritance = false

            methodName = DummyToken
            methodType = DummyToken

            attrName = DummyToken
            attrType = DummyToken

            params = mutableMapOf<String, Token>()
            modifier = DummyToken
        }
    }

    fun checkStatements() {

    }

    fun consolidate() {

    }

}