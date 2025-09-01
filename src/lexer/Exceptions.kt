package lexer

abstract class LexicalException(
    override val message: String,
    val lexeme: String,
    val lineNumber: Int,
    val columnNumber: Int
): Exception(message) {
    override fun toString(): String {
        return "[Error:$lexeme|$lineNumber]"
    }

    fun errorReport(): String {
        return "Error léxico en la línea $lineNumber, columna $columnNumber: $message\n" +
                "Detalle: $lexeme\n" +
                " ".repeat(9 + lexeme.length - 1) + "^\n" +
                this
    }
}

class InvalidCharacterException(
    message: String,
    lexeme: String,
    lineNumber: Int,
    columnNumber: Int
): LexicalException(message, lexeme, lineNumber, columnNumber)

class IntLiteralTooLongException(
    message: String,
    lexeme: String,
    lineNumber: Int,
    columnNumber: Int
): LexicalException(message, lexeme, lineNumber, columnNumber)

class EmptyCharException(
    message: String,
    lexeme: String,
    lineNumber: Int,
    columnNumber: Int
): LexicalException(message, lexeme, lineNumber, columnNumber)

class UnendedCharException(
    message: String,
    lexeme: String,
    lineNumber: Int,
    columnNumber: Int
): LexicalException(message, lexeme, lineNumber, columnNumber)

class NewLineException(
    message: String,
    lexeme: String,
    lineNumber: Int,
    columnNumber: Int
): LexicalException(message, lexeme, lineNumber, columnNumber)

class InvalidScapedCharException(
    message: String,
    lexeme: String,
    lineNumber: Int,
    columnNumber: Int
): LexicalException(message, lexeme, lineNumber, columnNumber)

class MoreThanOneCharInLiteralException(
    message: String,
    lexeme: String,
    lineNumber: Int,
    columnNumber: Int
): LexicalException(message, lexeme, lineNumber, columnNumber)

class UnendedStringException(
    message: String,
    lexeme: String,
    lineNumber: Int,
    columnNumber: Int
): LexicalException(message, lexeme, lineNumber, columnNumber)

class UnendedCompoundOperatorException(
    message: String,
    lexeme: String,
    lineNumber: Int,
    columnNumber: Int
): LexicalException(message, lexeme, lineNumber, columnNumber)


