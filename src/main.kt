import lexer.LexicalAnalyzer
import sourcemanager.SourceManagerImpl
import java.io.FileNotFoundException
import java.io.IOException

fun main(args: Array<String>) {

    val sourceManager = SourceManagerImpl()
    val lexer = LexicalAnalyzer(sourceManager)

    try {
        sourceManager.open(args[0])

        //calls to lexer

        sourceManager.close()
    } catch (_: FileNotFoundException) {
        println("Error: archivo a compilar no encontrado.")
    } catch (_: IOException) {
        println("Error mientras durante la lectura del archivo especificado.")
    }
}