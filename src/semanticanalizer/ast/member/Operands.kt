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
    var token: Token

    fun check(expectedType: String?): String?
}

interface Call {
    var arguments: MutableList<Expression>
}

interface Chained: Primary {
    fun checkChained(receiverType: String?): String
}

class Primitive(override var token: Token): Operand {

    override fun toString():String {
        return token.lexeme
    }

    override fun printItselfAndChildren(nestingLevel: Int) {

    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Primitivo ($token)")
    }

    override fun check(expectedType: String?): String? {
        val type = when (token.type) {
            TokenType.INTEGER_LITERAL -> "int"
            TokenType.CHAR_LITERAL -> "char"
            TokenType.TRUE -> "boolean"
            TokenType.FALSE -> "boolean"
            TokenType.NULL -> null
            else -> throw Exception("No se debería llegar hasta acá")
        }

        if (expectedType != null && expectedType != type)
            if (type != null)
                throw TypeMismatchException(token, type, expectedType)
            else
                throw UnexpectedNullOperandException(token, expectedType)

        return type
    }
}

interface Primary : Operand {
    var chained: Chained?
    var parent: ASTMember

    override fun printItselfAndChildren(nestingLevel: Int) {
        print("\t".repeat(nestingLevel) + this)
    }
}

class ParenthesizedExpression(
    override var token: Token,
    override var parent: ASTMember
): Primary {
    lateinit var expression: Expression
    override var chained: Chained? = null

    override fun toString(): String {
        return "($expression${chained?.let { ").$it" } ?: ")"}"
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Expresion parentizada:")
        expression.printSubAST(nestingLevel + 1)
        chained?.let {
            println("\t".repeat(nestingLevel + 1) + "Encadenado:")
            it.printSubAST(nestingLevel + 1)
        }
    }

    override fun check(expectedType: String?): String? {
        var type = expression.check(null)

        type = chained?.checkChained(type) ?: type

        checkCompatibleTypes(expectedType, type, token)

        return type
    }
}

class LiteralPrimary(
    override var token: Token,
    override var parent: ASTMember,
    var containerClass: Class,
    var containerCallable: Callable
): Primary {
    override var chained: Chained? = null

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

    override fun check(expectedType: String?): String {
        var type = when (token.type) {
            TokenType.STRING_LITERAL -> "String"
            TokenType.THIS -> {
                if (containerCallable is Method && (containerCallable as Method).modifier.lexeme == "static")
                    throw AccessToInstanceMemberFromStaticContextException(
                        token,
                        "No se puede acceder a this desde un contexto estático"
                    )

                containerClass.token.lexeme
            }
            else -> throw Exception("No se debería llegar acá, algo salió mal")
        }

        type = chained?.checkChained(type) ?: type

        checkCompatibleTypes(expectedType, type, token)

        return type
    }
}

class VariableAccess(
    override var token: Token,
    override var parent: ASTMember,
    var containerClass: Class,
    var containerCallable: Callable,
    var containerBlock: Block
): Primary, Chained {
    override var chained: Chained? = null

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

    override fun check(expectedType: String?): String {
        var type = when (token.lexeme) {
            in containerBlock.visibleVariablesMap -> {
                containerBlock.visibleVariablesMap[token.lexeme]!!.type
            }
            in containerCallable.paramMap -> {
                containerCallable.paramMap[token.lexeme]!!.typeToken.lexeme
            }
            in containerClass.attributeMap -> {
                if (containerCallable is Method && (containerCallable as Method).modifier.lexeme == "static")
                    throw AccessToInstanceMemberFromStaticContextException(
                        token,
                        "No se puede acceder a atributos de instancia desde un contexto estático."
                    )

                containerClass.attributeMap[token.lexeme]!!.first().typeToken.lexeme
            }
            else -> throw InvalidVarAccessException(token)
        }

        type = chained?.checkChained(type) ?: type

        checkCompatibleTypes(expectedType, type, token)

        return type
    }

    override fun checkChained(receiverType: String?): String {
        if (receiverType == "void" || receiverType in primitiveTypesSet && receiverType != "String")
            throw InvalidChainingException(token)

        if (token.lexeme !in symbolTable.classMap[receiverType]!!.attributeMap)
            throw InvalidAttributeAccessException(token, receiverType!!)

        val type = symbolTable.classMap[receiverType]!!.attributeMap[token.lexeme]!!.first().typeToken.lexeme

        return chained?.checkChained(type) ?: type
    }
}

class MethodCall(
    override var token: Token,
    override var parent: ASTMember,
    var containerClass: Class,
    var containerCallable: Callable
): Call, Chained {
    override var chained: Chained? = null
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

    override fun check(expectedType: String?): String {
        if (token.lexeme !in containerClass.methodMap)
            throw InvalidCallException(
                token,
                "La clase ${containerClass.token} no tiene un método de nombre $token"
            )

        val methodCalled = containerClass.methodMap[token.lexeme]!!

        if ((containerCallable is Method) && methodCalled.modifier.lexeme != "static"
            && (containerCallable as Method).modifier.lexeme == "static")
            throw InvalidCallException(
                token,
                "No se pueden referenciar métodos de instancia desde un contexto estático."
            )


        var type: String = methodCalled.typeToken.lexeme

        val formalArguments = methodCalled.paramMap

        if (formalArguments.size != arguments.size)
            throw InvalidCallException(
                token,
                "Se esperaban ${formalArguments.size}, pero se recibieron ${arguments.size}"
            )

        arguments.forEachIndexed { index, it ->
            val argType = it.check(null)

            checkCompatibleTypes(
                formalArguments.values.toList().get(index).typeToken.lexeme,
                argType, token
            )
        }

        type = chained?.checkChained(type) ?: type

        checkCompatibleTypes(expectedType, type, token)

        return type
    }

    override fun checkChained(receiverType: String?): String {
        if (receiverType == "void")
            throw InvalidChainingException(token)

        if (receiverType in primitiveTypesSet && receiverType != "String")
            throw InvalidChainingException(token)

        if (token.lexeme !in symbolTable.classMap[receiverType]!!.methodMap)
            throw InvalidCallException(
                token,
                "No se puede resolver el método al que se quiere acceder"
            )

        val methodCalled = symbolTable.classMap[receiverType]!!.methodMap[token.lexeme]!!

        if (methodCalled.modifier.lexeme == "static")
            throw InvalidCallException(
                token,
                "No se puede resolver el método al que se quiere acceder"
            )

        val type = methodCalled.typeToken.lexeme

        val formalArguments = methodCalled.paramMap

        if (formalArguments.size != arguments.size)
            throw InvalidCallException(
                token,
                "Se esperaban ${formalArguments.size}, pero se recibieron ${arguments.size}"
            )

        arguments.forEachIndexed { index, it ->
            val argType = it.check(null)

            checkCompatibleTypes(
                formalArguments.values.toList().get(index).typeToken.lexeme,
                argType, token
            )
        }

        return chained?.checkChained(type)?: type
    }
}

class ConstructorCall(
    override var token: Token,
    override var parent: ASTMember
): Primary, Call {
    override lateinit var arguments: MutableList<Expression>
    override var chained: Chained? = null

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

    override fun check(expectedType: String?): String {
        var type = token.lexeme

        if (token.lexeme !in symbolTable.classMap)
            throw NonExistentClassException(token, "La clase cuyo constructor se quiere invocar no existe" )

        val formalArguments = symbolTable.classMap[type]!!.constructor.paramMap

        if (formalArguments.size != arguments.size)
            throw InvalidCallException(
                token,
                "Se esperaban ${formalArguments.size}, pero se recibieron ${arguments.size}"
            )

        arguments.forEachIndexed { index, it ->
            val argType = it.check(null)

            checkCompatibleTypes(
                formalArguments.values.toList().get(index).typeToken.lexeme,
                argType, token
            )
        }

        type = chained?.checkChained(type) ?: type

        checkCompatibleTypes(expectedType, type, token)

        return type
    }
}

class StaticMethodCall(
    override var parent: ASTMember,
    override var token: Token,
    var calledMethodToken: Token
): Primary, Call {

    override lateinit var arguments: MutableList<Expression>
    override var chained: Chained? = null

    override fun toString(): String {
        return "$token.$calledMethodToken(${arguments})${chained?.let { ".$it" } ?: ""}"
    }

    override fun printSubAST(nestingLevel: Int) {
        println("\t".repeat(nestingLevel) + "Llamada a método estático ($token.$calledMethodToken):")
        println("\t".repeat(nestingLevel + 1) + "Argumentos:")
        arguments.forEach {
            it.printSubAST(nestingLevel + 1)
        }
        chained?.let {
            println("\t".repeat(nestingLevel + 1) + "Encadenado:")
            it.printSubAST(nestingLevel + 1)
        }
    }

    override fun check(expectedType: String?): String {
        if (token.lexeme !in symbolTable.classMap)
            throw NonExistentClassException(
                token,
                "No se puede resolver la clase a la que se está referenciando."
            )

        val calledClass = symbolTable.classMap[token.lexeme]!!

        if (calledMethodToken.lexeme !in calledClass.methodMap
            || calledClass.methodMap[calledMethodToken.lexeme]!!.modifier.lexeme != "static")
            throw InvalidCallException(
                calledMethodToken,
                "No existe un método estático con de nombre $calledMethodToken en la clase $token."
            )

        val calledMethod = calledClass.methodMap[calledMethodToken.lexeme]!!

        var type = calledMethod.typeToken.lexeme

        val formalArguments = calledMethod.paramMap

        if (formalArguments.size != arguments.size)
            throw InvalidCallException(
                calledMethodToken,
                "Se esperaban ${formalArguments.size}, pero se recibieron ${arguments.size}"
            )

        arguments.forEachIndexed { index, it ->
            val argType = it.check(null)

            checkCompatibleTypes(
                formalArguments.values.toList().get(index).typeToken.lexeme,
                argType, token
            )
        }

        type = chained?.checkChained(type) ?: type

        checkCompatibleTypes(expectedType, type, token)

        return type
    }
}

fun checkCompatibleTypes(expectedType: String?, actualType: String?, token: Token) {
    if (expectedType != null) {
        if (expectedType in primitiveTypesSet && expectedType != "String") {
            if (actualType == null)
                throw UnexpectedNullOperandException(token, expectedType)

            if (actualType !in primitiveTypesSet)
                throw TypeMismatchException(token, actualType, expectedType)

            if (actualType != expectedType)
                throw TypeMismatchException(token, actualType, expectedType)
        } else {
            if (actualType != null) {
                if (actualType in primitiveTypesSet && actualType != "String")
                    throw TypeMismatchException(token, actualType, expectedType)

                if (expectedType !in symbolTable.classMap[actualType]!!.ancestors) {
                    println(symbolTable.classMap[actualType]!!.ancestors)
                    println(expectedType)

                    throw TypeMismatchException(token, actualType, expectedType)
                }
            }
        }
    }
}