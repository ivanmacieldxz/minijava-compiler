package semanticanalizer.ast.member

import utils.Token

class UnaryOperator(var token: Token) {

    override fun toString(): String {
        return token.lexeme
    }

}

class BinaryOperator(var token: Token) {

    override fun toString(): String {
        return token.lexeme
    }

}