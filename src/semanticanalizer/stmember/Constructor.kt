package semanticanalizer.stmember

import fileWriter
import semanticanalizer.ast.member.Block
import utils.Token
import utils.Token.DummyToken
import java.io.FileWriter
import java.util.Collections

class Constructor(
    override var token: Token = DummyToken,
    override val parentClass: Class
) : ClassMember, Callable {

    override var declarationCompleted = false

    override var paramMap: MutableMap<String, FormalArgument> = Collections.emptyMap()
    override var block: Block? = null

    override fun toString(): String {
        return paramMap.toString() + "\n"
    }

    override fun isWellDeclared() {
        paramMap.values.forEach {
            it.isWellDeclared()
        }
    }

    override fun generateCode() {
        fileWriter.writeLabeledInstruction(getCodeLabel(), "LOADFP")
        fileWriter.write("LOADSP")
        fileWriter.write("STOREFP")
        fileWriter.write("FMEM ${paramMap.size}")
        fileWriter.write("STOREFP")
        fileWriter.write("RET 1")
    }

    override fun getCodeLabel(): String = "constructor@${parentClass.token.lexeme}"

    fun isDefaultConstructor(): Boolean {
        return token == DummyToken
    }

}