import lexer.LexicalAnalyzer
import lexer.LexicalAnalyzerImpl
import lexer.LexicalException
import parser.SyntacticAnalyzerItrImpl
import parser.SyntacticException
import semanticanalizer.SemanticException
import semanticanalizer.SymbolTable
import semanticanalizer.stmember.Object
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

        //printAST()

        symbolTable.checkSentences()

    } catch (e: LexicalException) {
        print(e.errorReport())
        wereErrors = true
    } catch (e: SyntacticException) {
        print(e)
        wereErrors = true
    } catch (e: SemanticException) {
        e.printStackTrace()
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

private fun printAST() {

    symbolTable.classMap.forEach { (className, classVal) ->
        if (className !in SymbolTable.classesNames) {
            println("class $className:")
            classVal.methodMap.forEach { (methodName, method) ->
                if (methodName !in Object.methodMap.keys) {
                    println("\t${method.modifier.lexeme.takeIf { lexeme -> lexeme != "" }?.plus(" ") ?: ""}${method.typeToken.lexeme} " +
                            "${method.token.lexeme}:")
                    method.printSubAST()
                }
            }
        }
    }
}