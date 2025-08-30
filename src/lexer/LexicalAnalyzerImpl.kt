package lexer

import sourcemanager.SourceManager
import token.Token
import lexer.State.*
import sourcemanager.SourceManager.END_OF_FILE
import token.TokenType

class LexicalAnalyzerImpl(
    private val sourceManager: SourceManager
): LexicalAnalyzer {

    private var goToNextChar = true
    private var lexerState = IDLE

    private var lexeme = ""
    private var currentChar = ' '
    private var token: Token? = null

    override fun getNextToken(): Token {
        token = null
        lexerState = IDLE
        lexeme = ""

        while (token == null) {

            if (goToNextChar) {
                currentChar = sourceManager.nextChar
            }

            //TODO: considerar eof para estados que no devuelven al encontrar un caracter que no puede reconocer
            //en particular, sería para los autómatas de strings y chars, que puede ser que no hayan terminado de formarse para cuando llegue el eof
            when (lexerState) {
                IDLE -> {
                    lexeme += currentChar
                    goToNextChar = true
                    when {
                        currentChar.isUpperCase() -> {
                            lexerState = BUILDING_CLASS_IDENTIFIER
                        }
                        currentChar.isLowerCase() -> {
                            lexerState = BUILDING_IDENTIFIER_OR_KEYWORD
                        }
                        currentChar.isDigit() -> {
                            lexerState = BUILDING_INTEGER_CONSTANT
                        }
                        currentChar == '/' -> {
                            lexeme += currentChar
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
                        currentChar in LexicalAnalyzer.OPERATORS -> {
                            when (currentChar) {
                                '%' -> buildToken(TokenType.MODULUS)
                                '*' -> buildToken(TokenType.MULTIPLICATION)
                                else -> {
                                    lexerState = BUILDING_OPERATOR
                                }
                            }
                        }
                        currentChar in LexicalAnalyzer.PUNCTUATION -> {
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
                        currentChar.isWhitespace() -> {
                            lexeme = ""
                        }
                        else -> {
                            lexeme = ""
                            throw Exception("Error léxico, caracter inválido" + currentChar.code)
                        }
                    }
                }
                POTENTIALLY_READING_COMMENT -> {
                    when (currentChar) {
                        '/' -> {
                            lexeme = ""
                            lexerState = READING_SINGLELINE_COMMENT
                        }
                        '*' -> {
                            lexeme = ""
                            lexerState = READING_MULTILINE_COMMENT
                        }
                        else -> {
                            goToNextChar = false
                            buildToken(TokenType.DIVISION)
                        }
                    }
                }
                READING_SINGLELINE_COMMENT -> {
                    when (currentChar) {
                        '\n' -> {
                            lexerState = IDLE
                        }
                    }
                }
                READING_MULTILINE_COMMENT -> {
                    when (currentChar) {
                        '*' -> {
                            lexerState = CLOSING_MULTILINE_COMMENT
                        }
                    }
                }
                CLOSING_MULTILINE_COMMENT -> {
                    lexerState = when (currentChar) {
                        '/' -> {
                            IDLE
                        }
                        else -> {
                            READING_MULTILINE_COMMENT
                        }
                    }
                }
                BUILDING_CLASS_IDENTIFIER -> {
                    when {
                        !(currentChar.isUpperCase() || currentChar.isDigit() || currentChar.isLowerCase() || currentChar == '_') -> {
                            goToNextChar = false
                            buildToken(TokenType.CLASS_IDENTIFIER)
                        }
                        else -> {
                            lexeme += currentChar
                        }
                    }
                }
                BUILDING_IDENTIFIER_OR_KEYWORD -> {
                    //todo: keywords
                    when {
                        !(currentChar.isUpperCase() || currentChar.isDigit() || currentChar.isLowerCase() || currentChar == '_') -> {
                            goToNextChar = false
                            buildToken(TokenType.MET_VAR_IDENTIFIER)
                        }
                        else -> {
                            lexeme += currentChar
                        }
                    }
                }
                BUILDING_INTEGER_CONSTANT -> {
                    when {
                        currentChar.isDigit().not() -> {
                            goToNextChar = false
                            buildToken(TokenType.INTEGER_CONSTANT)
                        }
                        else -> {
                            lexeme += currentChar
                        }
                    }
                }
                BUILDING_CHAR_CONSTANT -> {
                    when (currentChar) {
                        '\'' -> {
                            throw Exception("Error léxico: char vacío")
                        }
                        '\\' -> {
                            lexeme += currentChar
                            lexerState = BUILDING_SCAPED_CHAR_CONSTANT
                        }
                        END_OF_FILE -> {
                            throw Exception("Error léxico: char no terminado")
                        }
                        '\n' -> {
                            throw Exception("Error léxico: no se admiten saltos de línea dentro de un literal char")
                        }
                        else -> {
                            lexeme += currentChar
                            lexerState = CLOSING_CHAR_CONSTANT
                        }
                    }
                }
                BUILDING_SCAPED_CHAR_CONSTANT -> {
                    when (currentChar) {
                        END_OF_FILE -> {
                            throw Exception("Error léxico: char no terminado")
                        }
                        '\n' -> {
                            throw Exception("Error léxico: no se admiten saltos de línea dentro de un literal char")
                        }
                        ' ' -> {
                            throw Exception("Error léxico, no se admiten espacios como caracteres escapados")
                        }
                        '\t' -> throw Exception("Error léxico, no se admiten tabulaciones como caracteres escapados")
                        else -> {
                            lexeme += currentChar
                            lexerState = CLOSING_CHAR_CONSTANT
                        }
                    }
                }
                CLOSING_CHAR_CONSTANT -> {
                    when (currentChar) {
                        '\'' -> {
                            lexeme += currentChar
                            buildToken(TokenType.CHAR_CONSTANT)
                        }
                        END_OF_FILE -> {
                            throw Exception("Error léxico: char no terminado")
                        }
                        '\n' -> {
                            throw Exception("Error léxico: no se admiten saltos de línea dentro de un literal char")
                        }
                        else -> {
                            throw Exception("Error léxico: no se admite más de un caracter dentro de un char")
                        }
                    }
                }
                BUILDING_STRING_CONSTANT -> {
                    when (currentChar) {
                        END_OF_FILE -> {
                            throw Exception("Error léxico: string no terminado")
                        }
                        '\n' -> {
                            throw Exception("Error léxico: no se admiten saltos de línea dentro de un literal string")
                        }
                        '\"' -> {
                            lexeme += currentChar
                            buildToken(TokenType.STRING_CONSTANT)
                        }
                        '\\' -> {
                            lexeme += currentChar
                            lexerState = BUILDING_SCAPED_STRING_CONSTANT
                        }
                        else -> {
                            lexeme += currentChar
                        }
                    }
                }
                BUILDING_SCAPED_STRING_CONSTANT -> {
                    when (currentChar) {
                        ' ', '\t' -> {
                            throw Exception("Error léxico, no se admiten espacios o tabulaciones como caracteres escapados")
                        }
                        END_OF_FILE -> {
                            throw Exception("Error léxico: String no terminado")
                        }
                        else -> {
                            lexeme += currentChar
                            lexerState = BUILDING_STRING_CONSTANT
                        }
                    }
                }
                BUILDING_OPERATOR -> {
                    when (currentChar) {
                        '=' -> {
                            when (lexeme) {
                                ">", "<", "!", "=" -> {
                                    lexeme += currentChar
                                }
                                else -> {
                                    goToNextChar = false
                                }
                            }
                        }
                        '&' -> {
                            when (lexeme) {
                                "&" -> {
                                    lexeme += currentChar
                                }
                                else -> {
                                    goToNextChar = false
                                    throw Exception("Excepción Léxica: símbolo mal formado")
                                }
                            }
                        }
                        '|' -> {
                            when (lexeme) {
                                "|" -> {
                                    lexeme += currentChar
                                }
                                else -> {
                                    goToNextChar = false
                                    throw Exception("Excepción Léxica: símbolo mal formado")
                                }
                            }
                        }
                        '+' -> {
                            when (lexeme) {
                                "+" -> {
                                    lexeme += currentChar
                                }
                                else -> {
                                    goToNextChar = false
                                }
                            }
                        }
                        '-' -> {
                            when (lexeme) {
                                "-" -> {
                                    lexeme += currentChar
                                }
                                else -> {
                                    goToNextChar = false
                                }
                            }
                        }
                        else -> {
                            goToNextChar = false
                        }
                    }

                    when (lexeme) {
                        ">" -> {
                            buildToken(TokenType.GREATER_THAN)
                        }
                        ">=" -> {
                            buildToken(TokenType.GREATER_THAN_OR_EQUAL)
                        }
                        "<" -> {
                            buildToken(TokenType.LESS_THAN)
                        }
                        "<=" -> {
                            buildToken(TokenType.LESS_THAN_OR_EQUAL)
                        }
                        "=" -> {
                            buildToken(TokenType.ASSIGNMENT)
                        }
                        "==" -> {
                            buildToken(TokenType.EQUALS)
                        }
                        "!=" -> {
                            buildToken(TokenType.DIFFERENT)
                        }
                        "!" -> {
                            buildToken(TokenType.NOT)
                        }
                        "&&" -> {
                            buildToken(TokenType.AND)
                        }
                        "||" -> {
                            buildToken(TokenType.OR)
                        }
                        "+" -> {
                            buildToken(TokenType.ADDITION)
                        }
                        "++" -> {
                            buildToken(TokenType.INCREMENT)
                        }
                        "-" -> {
                            buildToken(TokenType.SUBSTRACTION)
                        }
                        "--" -> {
                            buildToken(TokenType.DECREMENT)
                        }
                    }

                    lexerState = IDLE
                }
            }
        }

        return token!!
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