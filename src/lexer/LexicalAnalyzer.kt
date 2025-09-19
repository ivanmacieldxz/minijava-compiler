package lexer

import utils.Token

interface LexicalAnalyzer {

    companion object {
        const val OPERATORS = "><!=&|+-*%"
        const val PUNCTUATION = "(){};,.:"
    }

    fun getNextToken(): Token

}