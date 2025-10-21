import lexer.LexicalAnalyzer
import lexer.LexicalAnalyzerImpl
import lexer.LexicalException
import parser.SyntacticAnalyzerItrImpl
import parser.SyntacticException
import semanticanalizer.stmember.SemanticException
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
        parser.start()

        symbolTable.checkDeclarations()
        symbolTable.consolidate()

        symbolTable.classMap.values.forEach { cls ->
            if ((cls.token.lexeme in SymbolTable.classesNames).not()) {
                println("${cls.modifier.lexeme.takeIf { it != "" }?.plus(" ") ?: ""}class ${cls.token.lexeme}:")
                cls.methodMap.values.forEach {
                    println("\t${it.modifier.lexeme.takeIf { it != "" }?.plus(" ") ?: ""}${it.typeToken.lexeme} " +
                            "${it.token.lexeme}:")
                    it.printBlock(2)
                }
            }
        }
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