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
    private var columnNumber = 0
    private var resetColumnNumber = false
    private var currentLine = ""

    override fun getNextToken(): Token {
        token = null
        lexerState = IDLE
        lexeme = ""

        while (token == null) {

            //todo: ver que onda con que no reinicia el contador de columna cuando se encuentra un salto de línea (especialmente en los comentarios
            if (resetColumnNumber)
                columnNumber = 0

            if (currentChar == '\n')
                currentLine = ""

            if (goToNextChar) {
                currentChar = sourceManager.nextChar
                columnNumber++
                resetColumnNumber = currentChar == '\n'
                currentLine += currentChar
            }

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
                                '{' -> buildToken(TokenType.LEFT_CURLY_BRACKET)
                                '}' -> buildToken(TokenType.RIGHT_CURLY_BRACKET)
                                ';' -> buildToken(TokenType.SEMICOLON)
                                ',' -> buildToken(TokenType.COMMA)
                                '.' -> buildToken(TokenType.DOT)
                                ':' -> buildToken(TokenType.COLON)
                            }
                        }
                        currentChar == '\n' -> {
                            lexeme = ""
                            columnNumber = 0
                        }
                        currentChar.isWhitespace() -> {
                            lexeme = ""
                        }
                        else -> {
                            throw InvalidCharacterException(
                                "$currentChar no es un símbolo válido.",
                                lexeme,
                                sourceManager.lineNumber,
                                columnNumber,
                                currentLine
                            )
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
                    when {
                        !(currentChar.isUpperCase() || currentChar.isDigit() || currentChar.isLowerCase() || currentChar == '_') -> {
                            goToNextChar = false

                            when (lexeme) {
                                "class" -> buildToken(TokenType.CLASS)
                                "extends" -> buildToken(TokenType.EXTENDS)
                                "public" -> buildToken(TokenType.PUBLIC)
                                "static" -> buildToken(TokenType.STATIC)
                                "void" -> buildToken(TokenType.VOID)
                                "boolean" -> buildToken(TokenType.BOOLEAN)
                                "char" -> buildToken(TokenType.CHAR)
                                "int" -> buildToken(TokenType.INT)
                                "abstract" -> buildToken(TokenType.ABSTRACT)
                                "final" -> buildToken(TokenType.FINAL)
                                "if" -> buildToken(TokenType.IF)
                                "else" -> buildToken(TokenType.ELSE)
                                "while" -> buildToken(TokenType.WHILE)
                                "return" -> buildToken(TokenType.RETURN)
                                "var" -> buildToken(TokenType.VAR)
                                "this" -> buildToken(TokenType.THIS)
                                "new" -> buildToken(TokenType.NEW)
                                "null" -> buildToken(TokenType.NULL)
                                "true" -> buildToken(TokenType.TRUE)
                                "false" -> buildToken(TokenType.FALSE)
                                else -> buildToken(TokenType.MET_VAR_IDENTIFIER)
                            }
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
                            if (lexeme.length > 9) {
                                throw IntLiteralTooLongException(
                                    "el literal entero supera la longitud máxima de 9 dígitos",
                                    lexeme,
                                    sourceManager.lineNumber,
                                    columnNumber,
                                    currentLine
                                )
                            } else {
                                buildToken(TokenType.INTEGER_LITERAL)
                            }
                        }
                        else -> {
                            lexeme += currentChar
                        }
                    }
                }
                BUILDING_CHAR_CONSTANT -> {
                    lexeme += currentChar
                    when (currentChar) {
                        '\'' -> {
                            throw EmptyCharException(
                                "no se admiten literales char vacíos.",
                                lexeme,
                                sourceManager.lineNumber,
                                columnNumber,
                                currentLine
                            )
                        }
                        '\\' -> {
                            lexerState = BUILDING_SCAPED_CHAR_CONSTANT
                        }
                        END_OF_FILE -> {
                            lexeme = lexeme.substring(0, lexeme.length - 1)
                            throw UnfinishedCharException(
                                "literal char mal formado. Se esperaba un <carácter>', pero se encontró EOF.",
                                lexeme,
                                sourceManager.lineNumber,
                                columnNumber,
                                currentLine
                            )
                        }
                        '\n' -> {
                            lexeme = lexeme.substring(0, lexeme.length - 1)
                            throw NewLineException(
                                "no se admiten saltos de línea dentro de un literal char.",
                                lexeme,
                                sourceManager.lineNumber,
                                columnNumber,
                                currentLine.replace("\n", " ")
                            )
                        }
                        else -> {
                            lexerState = CLOSING_CHAR_CONSTANT
                        }
                    }
                }
                BUILDING_SCAPED_CHAR_CONSTANT -> {
                    lexeme += currentChar
                    when (currentChar) {
                        END_OF_FILE -> {
                            lexeme = lexeme.substring(0, lexeme.length - 1)
                            throw UnfinishedCharException(
                                "literal char mal formado, se esperaba un <carácter>', pero se encontró EOF.",
                                lexeme,
                                sourceManager.lineNumber,
                                columnNumber,
                                currentLine
                            )
                        }
                        '\n' -> {
                            lexeme = lexeme.substring(0, lexeme.length - 1)
                            throw NewLineException(
                                "no se admiten saltos de línea dentro de un literal char.",
                                lexeme,
                                sourceManager.lineNumber,
                                columnNumber,
                                currentLine.replace("\n", " "),
                            )
                        }
                        ' ' -> {
                            throw InvalidScapedCharException(
                                "no se admiten espacios como caracteres escapados.",
                                lexeme,
                                sourceManager.lineNumber,
                                columnNumber,
                                currentLine
                            )
                        }
                        '\t' -> throw InvalidScapedCharException(
                            "no se admiten tabulaciones como caracteres escapados.",
                            lexeme,
                            sourceManager.lineNumber,
                            columnNumber,
                            currentLine
                        )
                        else -> {
                            lexerState = CLOSING_CHAR_CONSTANT
                        }
                    }
                }
                CLOSING_CHAR_CONSTANT -> {
                    lexeme += currentChar
                    when (currentChar) {
                        '\'' -> {
                            buildToken(TokenType.CHAR_LITERAL)
                        }
                        END_OF_FILE -> {
                            lexeme = lexeme.substring(0, lexeme.length - 1)
                            throw UnfinishedCharException(
                                "literal char no cerrado. Se esperaba ', pero se encontró EOF.",
                                lexeme,
                                sourceManager.lineNumber,
                                columnNumber,
                                currentLine
                            )
                        }
                        '\n' -> {
                            lexeme = lexeme.substring(0, lexeme.length - 1)
                            throw NewLineException(
                                "literal char no cerrado. Se esperaba ', pero se encontró un salto de línea.",
                                lexeme,
                                sourceManager.lineNumber,
                                columnNumber,
                                currentLine.replace("\n", " "),
                            )
                        }
                        else -> {
                            throw MoreThanOneCharInLiteralException(
                                "literal char mal formado: no se admite más de un caracter dentro de un literal char.",
                                lexeme,
                                sourceManager.lineNumber,
                                columnNumber,
                                currentLine
                            )
                        }
                    }
                }
                BUILDING_STRING_CONSTANT -> {
                    lexeme += currentChar
                    when (currentChar) {
                        END_OF_FILE -> {
                            lexeme = lexeme.substring(0, lexeme.length - 1)
                            throw UnfinishedStringException(
                                "literal string no cerrado: se esperaba \", pero se encontró EOF.",
                                lexeme,
                                sourceManager.lineNumber,
                                columnNumber,
                                currentLine
                            )
                        }
                        '\n' -> {
                            lexeme = lexeme.substring(0, lexeme.length - 1)
                            throw NewLineException(
                                "no se admiten saltos de línea dentro de un literal string.",
                                lexeme,
                                sourceManager.lineNumber,
                                columnNumber,
                                currentLine.replace("\n", " "),
                            )
                        }
                        '\"' -> {
                            buildToken(TokenType.STRING_LITERAL)
                        }
                        '\\' -> {
                            lexerState = BUILDING_SCAPED_STRING_CONSTANT
                        }
                    }
                }
                BUILDING_SCAPED_STRING_CONSTANT -> {
                    lexeme += currentChar
                    when (currentChar) {
                        ' ', '\t' -> {
                            throw InvalidScapedCharException(
                                "no se admiten espacios ni tabulaciones como caracteres escapados.",
                                lexeme,
                                sourceManager.lineNumber,
                                columnNumber,
                                currentLine
                            )
                        }
                        END_OF_FILE -> {
                            lexeme = lexeme.substring(0, lexeme.length - 1)
                            throw UnfinishedStringException(
                                "literal string no finalizado, se esperaba <caracter>\", pero se encontró EOF.",
                                lexeme,
                                sourceManager.lineNumber,
                                columnNumber,
                                currentLine
                            )
                        }
                        '\n' -> {
                            lexeme = lexeme.substring(0, lexeme.length - 1)
                            throw NewLineException(
                                "no se admiten saltos de línea dentro de un literal string",
                                lexeme,
                                sourceManager.lineNumber,
                                columnNumber,
                                currentLine.replace("\n", " "),
                            )
                        }
                        else -> {
                            lexerState = BUILDING_STRING_CONSTANT
                        }
                    }
                }
                BUILDING_OPERATOR -> {
                    when (currentChar) {
                        END_OF_FILE -> {
                            when (lexeme) {
                                "&" -> {
                                    throw UnfinishedCompoundOperatorException(
                                        "operador compuesto mal formado, se esperaba &, pero se encontró EOF.",
                                        lexeme,
                                        sourceManager.lineNumber,
                                        columnNumber,
                                        currentLine
                                    )
                                }
                                "|" -> {
                                    throw UnfinishedCompoundOperatorException(
                                        "operador compuesto mal formado, se esperaba |, pero se encontró EOF.",
                                        lexeme,
                                        sourceManager.lineNumber,
                                        columnNumber,
                                        currentLine
                                    )
                                }
                            }
                        }
                        '\n' -> {
                            when (lexeme) {
                                "|", "&" -> {
                                    goToNextChar = false
                                    throw UnfinishedCompoundOperatorException(
                                        "operador compuesto mal formado.",
                                        lexeme,
                                        sourceManager.lineNumber,
                                        columnNumber,
                                        currentLine.replace("\n", "")
                                    )
                                }
                            }
                        }
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
                                "&" -> lexeme += currentChar
                            }
                        }
                        '|' -> {
                            when (lexeme) {
                                "|" -> lexeme += currentChar
                            }
                        }
                        '+' -> {
                            when (lexeme) {
                                "+" -> {
                                    lexeme += currentChar
                                }
                            }
                        }
                        '-' -> {
                            when (lexeme) {
                                "-" -> {
                                    lexeme += currentChar
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
                        else -> {
                            goToNextChar = false
                            throw UnfinishedCompoundOperatorException(
                                "operador compuesto mal formado.",
                                lexeme +  currentChar,
                                sourceManager.lineNumber,
                                columnNumber,
                                currentLine.replace("\n", "")
                            )
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