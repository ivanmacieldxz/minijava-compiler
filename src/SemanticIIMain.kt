import lexer.LexicalAnalyzer
import lexer.LexicalAnalyzerImpl
import lexer.LexicalException
import outsourcemanager.CodeFileGeneratorImpl
import parser.SyntacticAnalyzerItrImpl
import parser.SyntacticException
import semanticanalizer.SemanticException
import semanticanalizer.SymbolTable
import semanticanalizer.stmember.Object
import sourcemanager.SourceManager
import sourcemanager.SourceManagerEfImpl

//lateinit var symbolTable: SymbolTable
//lateinit var fileWriter: CodeFileGenerator

fun main(args: Array<String>) {

    symbolTable = SymbolTable()

    val sourceManager: SourceManager = SourceManagerEfImpl()
    val lexer: LexicalAnalyzer = LexicalAnalyzerImpl(sourceManager)
    val parser = SyntacticAnalyzerItrImpl(lexer)
    fileWriter = CodeFileGeneratorImpl()
    var wereErrors = false


    try {
        //sourceManager.open("resources/sinErrores/${args[0]}")
        sourceManager.open(args[0])
        parser.start()

        symbolTable.checkDeclarations()
        symbolTable.consolidate()

        symbolTable.checkSentences()


//        symbolTable.classMap.values.forEach {
//
////            println("Ancestros de ${it.token.lexeme}: ${it.ancestors}")
////            println()
////
////            println("Atributos de instancia de ${it.token.lexeme}:")
////
////            it.attributeMap.values.forEach { attrSet ->
////                attrSet.forEach {
////                    println("\tNombre: ${it.token.lexeme}, clase de origen: ${it.parentClass.token.lexeme}, offset en CIR: ${it.offsetInCIR}")
////                }
////            }
//
//            println()
//
//            println("Métodos de instancia de ${it.token.lexeme}:")
//
//            it.methodMap.values.filter {
//                it.modifier.lexeme != "static"
//            }.forEach {
//                println("\tNombre: ${it.token.lexeme}, clase de origen: ${it.parentClass.token.lexeme}; offset en vtable: ${it.offsetInVTable}")
//            }
//
//            println()
//        }

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