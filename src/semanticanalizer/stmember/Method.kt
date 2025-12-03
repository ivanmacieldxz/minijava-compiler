package semanticanalizer.stmember

import fileWriter
import semanticanalizer.ast.member.Block
import symbolTable
import utils.Token
import utils.Token.DummyToken
import utils.TokenType
import utils.TokenType.CLASS_IDENTIFIER
import java.util.Collections

class Method(
    override var parentClass: Class
) : Modifiable, ClassMember, Callable, Typed {

    override lateinit var token: Token

    override var declarationCompleted = false

    override var modifier: Token = DummyToken

    override var paramMap: MutableMap<String, FormalArgument> = Collections.emptyMap()
    override var block: Block? = null

    override var typeToken: Token = DummyToken

    constructor(token: Token, parentClass: Class) : this(parentClass) {
        this.token = token
    }


    override fun toString(): String {
        return "$modifier\n $typeToken $token $paramMap"
    }

    override fun isWellDeclared() {
        typeToken.takeIf { it.type == CLASS_IDENTIFIER }?.let {
            symbolTable.classMap[it.lexeme]
                ?: throw UndeclaredClassException(
                    "La clase ${it.lexeme} del tipo de retorno del método no fue declarada previamente.",
                    it
                )
        }

        paramMap.values.forEach {
            it.isWellDeclared()
        }
    }

    override fun generateCode() {

        //TODO: no estoy considerando parámetros (me parece que no tengo que considerarlos igual
        // porque de eso se encarga el calee


        when (modifier.type) {
            TokenType.STATIC -> generateStaticMethodStackFrameCode()
            DummyToken.type -> generateInstanceMethodStackFrameCode()
            else -> {}
        }

        block?.generateCode()

        fileWriter.write("STOREFP")
        fileWriter.write("RET ${paramMap.size}")

    }

    private fun generateInstanceMethodStackFrameCode() {

    }

    private fun generateStaticMethodStackFrameCode() {
        fileWriter.writeLabeledInstruction(getCodeLabel(), "LOADFP")
        fileWriter.write("LOADSP")
        fileWriter.write("STOREFP")
    }

    fun equals(other: Method): Boolean {
        return token.lexeme == other.token.lexeme
    }
}