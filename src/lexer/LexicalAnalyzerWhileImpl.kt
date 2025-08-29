package lexer

import sourcemanager.SourceManager
import token.Token
import lexer.State.*
import sourcemanager.SourceManager.END_OF_FILE
import token.TokenType

class LexicalAnalyzerWhileImpl(
    private val sourceManager: SourceManager
): LexicalAnalyzer {

    private var goToNextChar = true
    private var lexerState = IDLE

    private var lexeme = ""
    private var currentChar = ' '
    private var token: Token? = null

    override fun getNextToken(): Token {
        token = null
        goToNextChar = true
        lexeme = ""

        while (token == null) {

            if (goToNextChar) {
                currentChar = sourceManager.nextChar
            }

            when (lexerState) {
                IDLE -> {
                    lexeme += currentChar
                    when {
                        currentChar.isUpperCase() -> {
                            lexerState = BUILDING_IDENTIFIER_CLASS_IDENTIFIER
                        }
                        currentChar.isLowerCase() -> {
                            lexerState = BUILDING_IDENTIFIER_OR_KEYWORD
                        }
                        currentChar.isDigit() -> {
                            lexerState = BUILDING_INTEGER_CONSTANT
                        }
                        currentChar == '/' -> {
                            lexerState = POTENTIALLY_READING_COMMENT
                        }
                        currentChar == '\'' -> {
                            lexerState = BUILDING_CHAR_CONSTANT
                        }
                        currentChar == '\"' -> {
                            lexerState = BUILDING_STRING_CONSTANT
                        }
                        currentChar == END_OF_FILE -> {
                            lexeme = ""
                            goToNextChar = false
                            token = Token.EOFToken
                        }
                        currentChar in LexicalAnalyzer.operators -> {
                            when (currentChar) {
                                '%' -> buildToken(TokenType.MODULUS)
                                '*' -> buildToken(TokenType.MULTIPLICATION)
                                else -> {
                                    lexerState = BUILDING_OPERATOR
                                }
                            }
                        }
                        currentChar in LexicalAnalyzer.punctuation -> {
                            when (currentChar) {
                                '(' -> buildToken(TokenType.LEFT_BRACKET)
                                ')' -> buildToken(TokenType.RIGHT_BRACKET)
                                '{' -> buildToken(TokenType.LEFT_SQUARE_BRACKET)
                                '}' -> buildToken(TokenType.RIGHT_SQUARE_BRACKET)
                                ';' -> buildToken(TokenType.SEMICOLON)
                                ',' -> buildToken(TokenType.COMMA)
                                '.' -> buildToken(TokenType.DOT)
                                ':' -> buildToken(TokenType.COLON)
                            }
                        }
                        else -> {
                            lexeme = ""
                            throw Exception("Error léxico, caracter inválido")
                        }
                    }
                }
                POTENTIALLY_READING_COMMENT -> {

                }
                READING_SINGLELINE_COMMENT -> TODO()
                READING_MULTINE_COMMENT -> TODO()
                BUILDING_IDENTIFIER_CLASS_IDENTIFIER -> TODO()
                BUILDING_IDENTIFIER_OR_KEYWORD -> TODO()
                BUILDING_INTEGER_CONSTANT -> TODO()
                BUILDING_CHAR_CONSTANT -> TODO()
                BUILDING_STRING_CONSTANT -> TODO()
                BUILDING_OPERATOR -> TODO()

            }
        }

        return token
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun buildToken(type: TokenType) {
        token = Token(
            type,
            lexeme,
            sourceManager.lineNumber
        )
    }


}