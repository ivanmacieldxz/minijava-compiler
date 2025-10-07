package semanticanalizer

import utils.Token
import utils.Token.DummyToken


class SymbolTable {

    val classMap = mutableMapOf<Token, Class>()
    var currentClass: Class = DummyClass
    var currentContext: Declarable = DummyContext
    val accumulator = Accumulator()

    class Accumulator {
        var className: Token = DummyToken
        var classModifier: Token = DummyToken
        var classParent: Token = DummyToken
        var foundInheritance = false

        var methodName: Token = DummyToken
        var methodModifier: Token = DummyToken
        var methodType: Token = DummyToken

        var attrName: Token = DummyToken
        var attrType: Token = DummyToken

        var params = mutableMapOf<String, Token>()
    }

    fun checkStatements() {

    }

    fun consolidate() {

    }

}