package semanticanalizer.stmember

import symbolTable
import utils.Token
import utils.Token.DummyToken
import utils.TokenType.CLASS_IDENTIFIER

class Attribute(
    override var token: Token = DummyToken,
    override val parentClass: Class
) : ClassMember, Typed {

    override var typeToken: Token = DummyToken
    override var declarationCompleted = false

    var offsetInCIR: Int = 0

    override fun toString(): String {
        return "$typeToken $token"
    }

    override fun isWellDeclared() {
        typeToken.takeIf { it.type == CLASS_IDENTIFIER }?.let {
            symbolTable.classMap[it.lexeme]
                ?: throw UndeclaredClassException(
                    "la clase ${it.lexeme} de la que el atributo es tipo no fue declarada previamente",
                    it
                )
        }
    }

    fun equals(other: Attribute): Boolean {
        return token.lexeme == other.token.lexeme
    }
}