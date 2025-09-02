package lexer

abstract class LexicalException(
    override val message: String,
    val lexeme: String,
    val lineNumber: Int,
    val columnNumber: Int,
    val line: String
): Exception(message) {
    override fun toString(): String {
        return "[Error:$lexeme|$lineNumber]"
    }

    open fun errorReport(): String {
        return "Error léxico en la línea $lineNumber, columna $columnNumber: $message\n" +
                "Detalle: $line\n" +
                " ".repeat(9 + line.length - 1) + "^\n" +
                this
    }
}

class InvalidCharacterException(
    message: String,
    lexeme: String,
    lineNumber: Int,
    columnNumber: Int,
    line: String
): LexicalException(message, lexeme, lineNumber, columnNumber, line)

class IntLiteralTooLongException(
    message: String,
    lexeme: String,
    lineNumber: Int,
    columnNumber: Int,
    line: String
): LexicalException(message, lexeme, lineNumber, columnNumber, line) {
    override fun errorReport(): String {
        return "Error léxico en la línea $lineNumber, columna $columnNumber: $message\n" +
                "Detalle: $line\n" +
                " ".repeat(9 + line.length - 2) + "^\n" +
                this
    }
}

class EmptyCharException(
    message: String,
    lexeme: String,
    lineNumber: Int,
    columnNumber: Int,
    line: String
): LexicalException(message, lexeme, lineNumber, columnNumber, line)

class UnfinishedCharException(
    message: String,
    lexeme: String,
    lineNumber: Int,
    columnNumber: Int,
    line: String
): LexicalException(message, lexeme, lineNumber, columnNumber, line)

class NewLineException(
    message: String,
    lexeme: String,
    lineNumber: Int,
    columnNumber: Int,
    line: String
): LexicalException(message, lexeme, lineNumber, columnNumber, line)

class InvalidScapedCharException(
    message: String,
    lexeme: String,
    lineNumber: Int,
    columnNumber: Int,
    line: String
): LexicalException(message, lexeme, lineNumber, columnNumber, line)

class MoreThanOneCharInLiteralException(
    message: String,
    lexeme: String,
    lineNumber: Int,
    columnNumber: Int,
    line: String
): LexicalException(message, lexeme, lineNumber, columnNumber, line)

class UnfinishedStringException(
    message: String,
    lexeme: String,
    lineNumber: Int,
    columnNumber: Int,
    line: String
): LexicalException(message, lexeme, lineNumber, columnNumber, line)

class UnfinishedCompoundOperatorException(
    message: String,
    lexeme: String,
    lineNumber: Int,
    columnNumber: Int,
    line: String
): LexicalException(message, lexeme, lineNumber, columnNumber, line)


