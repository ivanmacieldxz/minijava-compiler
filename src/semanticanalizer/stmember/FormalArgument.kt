package semanticanalizer.stmember

import symbolTable
import utils.Token
import utils.Token.DummyToken
import utils.TokenType.CLASS_IDENTIFIER

class FormalArgument(
    override var token: Token = DummyToken,
    override var typeToken: Token = DummyToken,
    var member: Callable
): Declarable, Typed {

    override var declarationCompleted = false

    override fun toString(): String {
        return "${typeToken.lexeme} ${token.lexeme}"
    }

    override fun isWellDeclared() {
        typeToken.takeIf { it.type == CLASS_IDENTIFIER }?.let {
            symbolTable.classMap[it.lexeme]
                ?: throw UndeclaredClassException(
                    "La clase ${it.lexeme} de la que el parámetro es tipo no está declarada.",
                    it
                )
        }
    }

}