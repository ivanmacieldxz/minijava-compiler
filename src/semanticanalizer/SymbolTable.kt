package semanticanalizer

import fileWriter
import semanticanalizer.stmember.Class
import semanticanalizer.stmember.Declarable
import semanticanalizer.stmember.DummyClass
import semanticanalizer.stmember.DummyContext
import semanticanalizer.stmember.FormalArgument
import semanticanalizer.stmember.Method
import semanticanalizer.stmember.Object
import semanticanalizer.stmember.StringClass
import semanticanalizer.stmember.System
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

    lateinit var mainMethod: Method

    var strLiteralsCount = 0

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

    fun checkSentences() {
        var foundMain = false

        classMap.values.forEach { cls ->
            cls.constructor.block?.check()

            cls.methodMap.values.forEach {
                if (foundMain.not()) {
                    foundMain = it.token.lexeme == "main" && it.modifier.lexeme == "static"
                    mainMethod = it
                } else if (it.token.lexeme == "main" && it.modifier.lexeme == "static")
                    throw object : SemanticException(
                        "Solo se admite un método main por archivo MiniJava.",
                        DummyToken
                    ){}

                if (cls.owns(it)) {
                    it.block?.check()
                }
            }
        }

        if (foundMain.not())
            throw object : SemanticException("No se encontró método main.", DummyToken) {}

    }

    fun generateCode() {
        fileWriter.writeCodeSectionHeader()
        fileWriter.writePush(mainMethod.getCodeLabel())
        fileWriter.writeCall()
        fileWriter.writeHalt()

        fileWriter.writeAuxRoutines()

        classMap.values.forEach {

            it.generateCode()

        }
    }

}