package lexer

import token.Token

interface LexicalAnalyzer {

    companion object {
        const val OPERATORS = "><!=&|+-*%"
        const val PUNCTUATION = "(){};,.:"
    }

    fun getNextToken(): Token

}