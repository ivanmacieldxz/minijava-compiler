package lexer

import token.Token

interface LexicalAnalyzer {

    companion object {
        const val operators = "><!=&|+-*"
        const val punctuation = "(){};,.:"
    }

    fun getNextToken(): Token

}