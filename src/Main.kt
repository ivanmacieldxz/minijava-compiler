import lexer.LexicalAnalyzer
import lexer.LexicalAnalyzerImpl
import sourcemanager.SourceManagerImpl
import token.Token
import java.io.FileNotFoundException
import java.io.IOException

fun main(args: Array<String>) {

    val sourceManager = SourceManagerImpl()
    val lexer: LexicalAnalyzer = LexicalAnalyzerImpl(sourceManager)
    lateinit var token: Token

    try {
        sourceManager.open(args[0])

        do {
            token = lexer.getNextToken()

            println("Token: (" + token.type.toString() + ", " + token.lexeme + ", " + token.lineNumber + ")")
        } while (token != Token.EOFToken)

        sourceManager.close()
    } catch (_: FileNotFoundException) {
        println("Error: archivo a compilar no encontrado.")
    } catch (_: IOException) {
        println("Error mientras durante la lectura del archivo especificado.")
    } catch (e: Exception) {
        e.printStackTrace()
    }

    println("[SinErrores]")
}