package semanticanalizer.ast.member

import semanticanalizer.ast.ASTMember
import semanticanalizer.stmember.Callable
import semanticanalizer.stmember.Class
import semanticanalizer.stmember.Method
import symbolTable
import utils.Token
import utils.TokenType
import kotlin.collections.contains
import kotlin.collections.first

interface Operand: ASTMember {
    fun check(type: String?): String
}

interface TokenizedOperand: Operand {
    var token: Token
}

interface Call {
    var arguments: MutableList<Expression>
}

class Primitive(
    override var token: Token
): TokenizedOperand {

    override fun toString():String {
        return token.lexeme
    }

    override fun printItselfAndChildren(nestingLevel: Int) {

    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Primitivo ($token)")
    }

    override fun check(type: String?): String =
        when (token.type) {
            TokenType.INTEGER_LITERAL-> "int"
            TokenType.CHAR_LITERAL -> "char"
            TokenType.TRUE -> "boolean"
            TokenType.FALSE -> "boolean"
            else -> throw Exception("No se debería llegar hasta acá")
        }
}

interface Primary : Operand {
    var chained: Primary?
    var parent: ASTMember

    override fun printItselfAndChildren(nestingLevel: Int) {
        print("\t".repeat(nestingLevel) + this)
    }
}

class ParenthesizedExpression(override var parent: ASTMember): Primary {
    var expression: Expression? = null
    override var chained: Primary? = null

    override fun toString(): String {
        return "($expression${chained?.let { ").$it" } ?: ")"}"
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Expresion parentizada:")
        expression?.printSubAST(nestingLevel + 1)
        chained?.let {
            println("\t".repeat(nestingLevel + 1) + "Encadenado:")
            it.printSubAST(nestingLevel + 1)
        }
    }

    override fun check(type: String?): String {
        val type = expression!!.check(type)

        val definitiveType = chained?.check(type) ?: type

        return definitiveType!!
    }
}

class LiteralPrimary(
    override var token: Token,
    override var parent: ASTMember,
    var containerClass: Class
): Primary, TokenizedOperand {
    override var chained: Primary? = null

    override fun toString():String {
        return "$token${chained?.let { ".$it" } ?: ""}"
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Primario literal ($token):")
        chained?.let {
            println("\t".repeat(nestingLevel + 1) + "Encadenado:")
            it.printSubAST(nestingLevel + 1)
        }
    }

    override fun check(type: String?): String {
        val type = when (token.type) {
            TokenType.STRING_LITERAL -> "String"
            TokenType.THIS -> containerClass.token.lexeme
            else -> throw Exception("No se debería llegar acá, algo salió mal")
        }

        val definitiveType = chained?.check(type) ?: type

        return definitiveType
    }
}

class VariableAccess(
    override var token: Token,
    override var parent: ASTMember,
    var containerClass: Class,
    var containerCallable: Callable,
    var containerBlock: Block?
): Primary, TokenizedOperand {
    override var chained: Primary? = null

    override fun toString(): String {
        return "$token${chained?.let { ".$it" } ?: ""}"
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Acceso var ($token):")
        chained?.let {
            println("\t".repeat(nestingLevel + 1) + "Encadenado:")
            it.printSubAST(nestingLevel + 1)
        }
    }

    override fun check(type: String?): String {
        val type: String = if (type == null) {
            if (token.lexeme !in containerBlock!!.visibleVariablesMap
                && token.lexeme !in containerCallable.paramMap
                && token.lexeme !in containerClass.attributeMap)
                throw Exception("No se puede resolver el símbolo $token")

            when (token.lexeme) {
                in containerBlock!!.visibleVariablesMap -> {
                    containerBlock!!.visibleVariablesMap[token.lexeme]!!.type
                }
                in containerCallable.paramMap -> {
                    containerCallable.paramMap[token.lexeme]!!.typeToken.lexeme
                }
                in containerClass.attributeMap -> {
                    containerClass.attributeMap[token.lexeme]!!.first().typeToken.lexeme
                }
                else -> throw Exception("Literalmente imposible que se llegue acá")
            }
        } else {
            //es acceso como encadenado
            if (token.lexeme !in symbolTable.classMap[type]!!.attributeMap)
                throw Exception("La clase $type no tiene un atributo $token")

            symbolTable.classMap[type]!!.attributeMap[token.lexeme]!!.first().typeToken.lexeme
        }

        return chained?.check(type) ?: type
    }
}

class MethodCall(
    override var token: Token,
    override var parent: ASTMember,
    var containerClass: Class
): Primary, TokenizedOperand, Call {
    override var chained: Primary? = null
    override lateinit var arguments: MutableList<Expression>

    override fun toString(): String {
        return "$token(${arguments})${chained?.let { ".$it" } ?: ""}"
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Llamada a método ($token):")
        println("\t".repeat(nestingLevel + 1) + "Argumentos:")
        arguments.forEach {
            it.printSubAST(nestingLevel + 1)
        }
        chained?.let {
            println("\t".repeat(nestingLevel + 1) + "Encadenado:")
            it.printSubAST(nestingLevel + 1)
        }
    }

    override fun check(type: String?): String {
        val type: String = if (type == null) {
            if (token.lexeme !in containerClass.methodMap)
                throw Exception("No se puede resolver el símbolo $token")

            containerClass.methodMap[token.lexeme]!!.typeToken.lexeme.takeIf {
                containerClass.methodMap[token.lexeme]!!.modifier.lexeme != "static"
            } ?: throw Exception("Los métodos static no pueden ser accedidos sin llamar a su clase.")

        } else {
            //es acceso como encadenado
            if (token.lexeme !in symbolTable.classMap[type]!!.methodMap)
                throw Exception("La clase $type no tiene un atributo $token")

            containerClass.methodMap[token.lexeme]!!.typeToken.lexeme.takeIf {
                containerClass.methodMap[token.lexeme]!!.modifier.lexeme != "static"
            } ?: throw Exception("Los métodos static no pueden ser encadenados.")
        }

        arguments.forEachIndexed { index, it ->
            val argType = it.check(null)

            if (argType != (parent as Callable).paramMap.toList().get(index).second.typeToken.lexeme)
                throw Exception("El tipo del parámetro actual no coincide con el tipo del parámetro formal.")
        }

        return chained?.check(type) ?: type
    }
}

class ConstructorCall(
    override var token: Token,
    override var parent: ASTMember
): Primary, TokenizedOperand, Call {
    override lateinit var arguments: MutableList<Expression>
    override var chained: Primary? = null

    override fun toString(): String {
        return "new $token(${arguments})${chained?.let { ".$it" } ?: ""}"
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Llamada a constructor ($token):")
        println("\t".repeat(nestingLevel + 1) + "Argumentos:")
        arguments.forEach {
            it.printSubAST(nestingLevel + 1)
        }
        chained?.let {
            println("\t".repeat(nestingLevel + 1) + "Encadenado:")
            it.printSubAST(nestingLevel + 1)
        }
    }

    override fun check(type: String?): String {
        val type = token.lexeme

        if (token.lexeme !in symbolTable.classMap)
            throw Exception("La clase cuyo constructor se quiere invocar no existe")

        arguments.forEachIndexed { index, it ->
            val argType = it.check(null)

            if (argType != (parent as Callable).paramMap.toList().get(index).second.typeToken.lexeme)
                throw Exception("El tipo del parámetro actual no coincide con el tipo del parámetro formal.")
        }

        return chained?.check(type) ?: type
    }
}

class StaticMethodCall(
    override var parent: ASTMember,
    var calledClass: Token,
    var calledMethod: Token
): Primary, Call {

    override lateinit var arguments: MutableList<Expression>
    override var chained: Primary? = null

    override fun toString():String {
        return "$calledClass.$calledMethod(${arguments})${chained?.let { ".$it" } ?: ""}"
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Llamada a método estático ($calledClass.$calledMethod):")
        println("\t".repeat(nestingLevel + 1) + "Argumentos:")
        arguments.forEach {
            it.printSubAST(nestingLevel + 1)
        }
        chained?.let {
            println("\t".repeat(nestingLevel + 1) + "Encadenado:")
            it.printSubAST(nestingLevel + 1)
        }
    }

    override fun check(type: String?): String {
        if (calledClass.lexeme !in symbolTable.classMap)
            throw Exception("La clase cuyo método se está referenciando no fue declarada.")

        val calledClass = symbolTable.classMap[calledClass.lexeme]!!

        if (calledMethod.lexeme !in calledClass.methodMap
            || calledClass.methodMap[calledMethod.lexeme]!!.modifier.lexeme != "static")
            throw Exception("No existe un método estático con ese nombre en la clase que se está referenciando.")

        val type = calledClass.methodMap[calledMethod.lexeme]!!.typeToken.lexeme

        arguments.forEachIndexed { index, it ->
            val argType = it.check(null)

            if (argType != (parent as Callable).paramMap.toList().get(index).second.typeToken.lexeme)
                throw Exception("El tipo del parámetro actual no coincide con el tipo del parámetro formal.")
        }

        return chained?.check(type) ?: type
    }
}