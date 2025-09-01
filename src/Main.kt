import lexer.LexicalAnalyzer
import lexer.LexicalAnalyzerImpl
import lexer.LexicalException
import sourcemanager.SourceManagerImpl
import token.Token
import java.io.FileNotFoundException
import java.io.IOException

fun main(args: Array<String>) {

    val sourceManager = SourceManagerImpl()
    val lexer: LexicalAnalyzer = LexicalAnalyzerImpl(sourceManager)
    var token: Token? = null
    var wereErrors = false

    try {
        sourceManager.open(args[0])

        do {
            try {
                token = lexer.getNextToken()

                println("Token: (" + token.type.toString() + ", " + token.lexeme + ", " + token.lineNumber + ")")
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