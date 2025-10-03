package lexer

import sourcemanager.SourceManager
import utils.Token
import lexer.State.*
import sourcemanager.SourceManager.END_OF_FILE
import utils.TokenType

class LexicalAnalyzerImpl(
    private val sourceManager: SourceManager
): LexicalAnalyzer {

    private var goToNextChar = true
    private var lexerState = IDLE

    private var lexeme = ""
    private var currentChar = ' '
    private var token: Token? = null
    private var currentLine = ""

    override fun getNextToken(): Token {
        token = null
        lexerState = IDLE
        lexeme = ""

        var unicodeCharNumbers = 0

        while (token == null) {

            if (lexerState != READING_MULTILINE_COMMENT && lexerState != CLOSING_MULTILINE_COMMENT && currentChar == '\n')
                currentLine = ""

            if (goToNextChar) {
                currentChar = sourceManager.nextChar
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
                            Token.EOFToken.lineNumber = sourceManager.lineNumber
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
                        }
                        currentChar.isWhitespace() -> {
                            lexeme = ""
                        }
                        else -> {
                            throw InvalidSymbolException(
                                "$currentChar no es un símbolo válido.",
                                lexeme,
                                sourceManager.lineNumber,
                                sourceManager.columnNumber,
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
                        '\n', END_OF_FILE -> {
                            lexerState = IDLE
                        }
                    }
                }
                READING_MULTILINE_COMMENT -> {
                    when (currentChar) {
                        '*' -> {
                            lexerState = CLOSING_MULTILINE_COMMENT
                        }
                        END_OF_FILE -> throw UnfinishedMultilineCommentException(
                            sourceManager.lineNumber,
                            sourceManager.columnNumber,
                            currentLine
                        )
                    }
                }
                CLOSING_MULTILINE_COMMENT -> {
                    lexerState = when (currentChar) {
                        '/' -> {
                            IDLE
                        }
                        END_OF_FILE -> throw UnfinishedMultilineCommentException(
                            sourceManager.lineNumber,
                            sourceManager.columnNumber,
                            currentLine
                        )
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
                                    sourceManager.columnNumber,
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
                                sourceManager.columnNumber,
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
                                sourceManager.columnNumber,
                                currentLine
                            )
                        }
                        '\n' -> {
                            lexeme = lexeme.substring(0, lexeme.length - 1)
                            throw NewLineException(
                                "no se admiten saltos de línea dentro de un literal char.",
                                lexeme,
                                sourceManager.lineNumber,
                                sourceManager.columnNumber,
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
                                sourceManager.columnNumber,
                                currentLine
                            )
                        }
                        '\n' -> {
                            lexeme = lexeme.substring(0, lexeme.length - 1)
                            throw NewLineException(
                                "no se admiten saltos de línea dentro de un literal char.",
                                lexeme,
                                sourceManager.lineNumber,
                                sourceManager.columnNumber,
                                currentLine.replace("\n", " "),
                            )
                        }
                        'u' -> {
                            lexerState = POTENTIALLY_READING_UNICODE_CHAR_CONSTANT
                            unicodeCharNumbers = 0
                        }
                        else -> {
                            lexerState = CLOSING_CHAR_CONSTANT
                        }
                    }
                }
                POTENTIALLY_READING_UNICODE_CHAR_CONSTANT -> {
                    lexeme += currentChar
                    when {
                        currentChar == '\'' -> {
                            when (unicodeCharNumbers) {
                                4 -> buildToken(TokenType.CHAR_LITERAL)
                                0 -> buildToken(TokenType.CHAR_LITERAL)
                                else -> throw InvalidUnicodeCharException(
                                    "literal char unicode mal formado, se esperaban 4 dígitos para unicode, " +
                                            "pero se encontraron solo ${unicodeCharNumbers}, ",
                                    lexeme,
                                    sourceManager.lineNumber,
                                    sourceManager.columnNumber,
                                    currentLine
                                )
                            }
                        }
                        currentChar == END_OF_FILE -> {
                            lexeme = lexeme.substring(0, lexeme.length - 1)
                            throw UnfinishedCharException(
                                "literal char mal formado, se esperaba un dígito o ', pero se encontró EOF.",
                                lexeme,
                                sourceManager.lineNumber,
                                sourceManager.columnNumber,
                                currentLine
                            )
                        }
                        currentChar == '\n' -> {
                            lexeme = lexeme.substring(0, lexeme.length - 1)
                            throw NewLineException(
                                "no se admiten saltos de línea dentro de un literal char.",
                                lexeme,
                                sourceManager.lineNumber,
                                sourceManager.columnNumber,
                                currentLine.replace("\n", " "),
                            )
                        }
                        currentChar.isDigit() -> {
                            lexerState = POTENTIALLY_READING_UNICODE_CHAR_CONSTANT
                            unicodeCharNumbers++
                            if (unicodeCharNumbers == 4)
                                lexerState = CLOSING_CHAR_CONSTANT
                        }
                        currentChar.isLetter() -> {
                            throw InvalidUnicodeCharException(
                                "literal char unicode mal formado, se esperaba un dígito, pero se encontró " +
                                        "un caracter, durante la formación del literal char unicode",
                                lexeme,
                                sourceManager.lineNumber,
                                sourceManager.columnNumber,
                                currentLine
                            )
                        }
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
                                sourceManager.columnNumber,
                                currentLine
                            )
                        }
                        '\n' -> {
                            lexeme = lexeme.substring(0, lexeme.length - 1)
                            throw NewLineException(
                                "literal char no cerrado. Se esperaba ', pero se encontró un salto de línea.",
                                lexeme,
                                sourceManager.lineNumber,
                                sourceManager.columnNumber,
                                currentLine.replace("\n", " "),
                            )
                        }
                        else -> {
                            if (unicodeCharNumbers == 4) {
                                if (currentChar.isDigit())
                                    throw InvalidUnicodeCharException(
                                        "demasiados dígitos en literal char unicode, se esperaba ', " +
                                                "pero se encontró un dígito",
                                        lexeme,
                                        sourceManager.lineNumber,
                                        sourceManager.columnNumber,
                                        currentLine
                                    )
                                else
                                    throw InvalidUnicodeCharException(
                                        "literal char unicode mal formado, se esperaba ', " +
                                                "pero se encontró $currentChar",
                                        lexeme,
                                        sourceManager.lineNumber,
                                        sourceManager.columnNumber,
                                        currentLine
                                    )
                            }

                            throw MoreThanOneCharInLiteralException(
                                "literal char mal formado: no se admite más de un carácter dentro de un literal char.",
                                lexeme,
                                sourceManager.lineNumber,
                                sourceManager.columnNumber,
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
                                sourceManager.columnNumber,
                                currentLine
                            )
                        }
                        '\n' -> {
                            lexeme = lexeme.substring(0, lexeme.length - 1)
                            throw NewLineException(
                                "no se admiten saltos de línea dentro de un literal string.",
                                lexeme,
                                sourceManager.lineNumber,
                                sourceManager.columnNumber,
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
                        END_OF_FILE -> {
                            lexeme = lexeme.substring(0, lexeme.length - 1)
                            throw UnfinishedStringException(
                                "literal string no finalizado, se esperaba <caracter>\", pero se encontró EOF.",
                                lexeme,
                                sourceManager.lineNumber,
                                sourceManager.columnNumber,
                                currentLine
                            )
                        }
                        '\n' -> {
                            lexeme = lexeme.substring(0, lexeme.length - 1)
                            throw NewLineException(
                                "no se admiten saltos de línea dentro de un literal string",
                                lexeme,
                                sourceManager.lineNumber,
                                sourceManager.columnNumber,
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
                                        sourceManager.columnNumber,
                                        currentLine
                                    )
                                }
                                "|" -> {
                                    throw UnfinishedCompoundOperatorException(
                                        "operador compuesto mal formado, se esperaba |, pero se encontró EOF.",
                                        lexeme,
                                        sourceManager.lineNumber,
                                        sourceManager.columnNumber,
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
                                        sourceManager.columnNumber,
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
                                else -> {
                                    goToNextChar = false
                                }
                            }
                        }
                        '|' -> {
                            when (lexeme) {
                                "|" -> lexeme += currentChar
                                else -> {
                                    goToNextChar = false
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
                        else -> {
                            goToNextChar = false
                            throw UnfinishedCompoundOperatorException(
                                "operador compuesto mal formado.",
                                lexeme +  currentChar,
                                sourceManager.lineNumber,
                                sourceManager.columnNumber,
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