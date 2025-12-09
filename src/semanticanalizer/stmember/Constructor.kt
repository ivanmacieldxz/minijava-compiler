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

        //TODO: no estoy considerando que si bien un constructor puede ser por defecto,
        //igual tiene que hacer las cosas que hace el constructor padre
        //SIEMPRE debe hacer las cosas que hace el constructor padre
        //TODO: esto no importa a menos que haga el logro de atributos inicializados me parece
        // en tal caso, se puede resolver haciendo que durante la consolidación, se le asigne el bloque
        // del padre como bloque
        // y en caso de que no sea el por defecto, que el bloque del constructor padre
        // sea la primera sentencia dentro del bloque del constructor hijo
        block?.generateCode() ?: {
            fileWriter.writeFreeLocalVars(0)
        }

        fileWriter.write("STOREFP")
        fileWriter.writeRet(paramMap.size + 1)
    }

    override fun getCodeLabel(): String = "constructor@${parentClass.token.lexeme}"

    fun isDefaultConstructor(): Boolean {
        return token == DummyToken
    }

}