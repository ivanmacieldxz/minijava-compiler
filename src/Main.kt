import lexer.LexicalAnalyzer
import lexer.LexicalAnalyzerImpl
import lexer.LexicalException
import parser.SyntacticAnalyzerItrImpl
import parser.SyntacticException
import semanticanalizer.SemanticException
import semanticanalizer.SymbolTable
import sourcemanager.SourceManager
import sourcemanager.SourceManagerEfImpl

lateinit var symbolTable: SymbolTable

fun main(args: Array<String>) {

    symbolTable = SymbolTable()

    val sourceManager: SourceManager = SourceManagerEfImpl()
    val lexer: LexicalAnalyzer = LexicalAnalyzerImpl(sourceManager)
    val parser = SyntacticAnalyzerItrImpl(lexer)
    var wereErrors = false


    try {
        sourceManager.open(args[0])
//        sourceManager.open("resources/conErrores/redefinicion1.java")
        parser.start()

        symbolTable.checkDeclarations()
        symbolTable.consolidate()
    } catch (e: LexicalException) {
        print(e.errorReport())
        wereErrors = true
    } catch (e: SyntacticException) {
        print(e)
        wereErrors = true
    } catch (e: SemanticException) {
        print(e)
        wereErrors = true
    } finally {
        sourceManager.close()
    }

    if (wereErrors.not()) {
        println("Compilación Exitosa")
        println()
        println("[SinErrores]")
    }

}