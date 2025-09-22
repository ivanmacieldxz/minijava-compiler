import lexer.LexicalAnalyzer
import lexer.LexicalAnalyzerImpl
import lexer.LexicalException
import parser.SyntacticAnalyzerItrImpl
import parser.SyntacticException
import sourcemanager.SourceManager
import sourcemanager.SourceManagerEfImpl


fun main(args: Array<String>) {

    val sourceManager: SourceManager = SourceManagerEfImpl()
    val lexer: LexicalAnalyzer = LexicalAnalyzerImpl(sourceManager)
    val parser = SyntacticAnalyzerItrImpl(lexer)
    var wereErrors = false

    try {
        sourceManager.open("resources/synt/sinErrores.java")

        parser.start()
    } catch (e: LexicalException) {
        print(e.errorReport())
        wereErrors = true
    } catch (e: SyntacticException) {
        print(e)
        wereErrors = true
    } finally {
        sourceManager.close()
    }

    if (wereErrors.not()) {
        println()
        println("[SinErrores]")
    }

}