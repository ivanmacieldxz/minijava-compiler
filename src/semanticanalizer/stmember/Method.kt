package semanticanalizer.stmember

import symbolTable
import utils.Token
import utils.Token.DummyToken
import utils.TokenType.CLASS_IDENTIFIER
import java.util.Collections

class Method(
    override var token: Token = DummyToken,
    override var parentClass: Class
) : Modifiable, ClassMember, Callable, Typed {

    override var paramMap: MutableMap<String, FormalArgument> = Collections.emptyMap()
    override var modifier: Token = DummyToken
    override var typeToken: Token = DummyToken
    override var declarationCompleted = false

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

    fun equals(other: Method): Boolean {
        return token.lexeme == other.token.lexeme
    }
}