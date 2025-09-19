import lexer.LexicalAnalyzer
import lexer.LexicalAnalyzerImpl
import lexer.LexicalException
import sourcemanager.SourceManager
import sourcemanager.SourceManagerEfImpl
import utils.Token
import java.io.FileNotFoundException
import java.io.IOException

fun main(args: Array<String>) {

    val sourceManager: SourceManager = SourceManagerEfImpl()
    val lexer: LexicalAnalyzer = LexicalAnalyzerImpl(sourceManager)
    var token: Token? = null
    var wereErrors = false

    try {
        sourceManager.open(args[0])

        do {
            try {
                token = lexer.getNextToken()

                println("(" + token.type + ", " + token.lexeme + ", " + token.lineNumber + ")")
            } catch (e: LexicalException) {
                wereErrors = true
                println(e.errorReport())
            }
        } while (token == null || token != Token.EOFToken)

    } catch (_: FileNotFoundException) {
        println("Error: archivo a compilar no encontrado.")
    } catch (_: IOException) {
        println("Error durante la lectura del archivo especificado.")
    } finally {
        sourceManager.close()
    }

    if (!wereErrors) {
        println()
        println("[SinErrores]")
    }
}