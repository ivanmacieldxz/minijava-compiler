package semanticanalizer.ast.member

import fileWriter
import semanticanalizer.ast.ASTMember
import semanticanalizer.stmember.Callable
import semanticanalizer.stmember.Class
import semanticanalizer.stmember.Method
import symbolTable
import utils.Token
import utils.TokenType
import kotlin.collections.contains
import kotlin.collections.first
import kotlin.collections.indexOf

interface Operand: ASTMember {
    var token: Token

    fun check(expectedType: String?): String?
}

interface Primary : Operand {
    var chained: Chained?
    var parent: ASTMember

    override fun printItselfAndChildren(nestingLevel: Int) {
        print("\t".repeat(nestingLevel) + this)
    }

    fun generateCodeWithoutChained(): String
}

interface Call: Primary {
    var arguments: MutableList<Expression>
}

interface Chained: Primary {
    fun checkChained(receiverType: String?): String
    fun generateAsChained(receiverType: String)
    fun generateCodeWithoutChained(receiverType: String): String
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

    override fun generateCode() {
        val primitiveValue: String = when (token.type) {
            TokenType.INTEGER_LITERAL -> token.lexeme
            TokenType.CHAR_LITERAL -> token.lexeme[0].code.toString()
            TokenType.TRUE -> "1"
            TokenType.FALSE -> "0"
            else -> "0      #NULL"
        }

        fileWriter.writePush(primitiveValue)
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

    override fun generateCode() {
        expression.generateCode()

        chained?.generateAsChained(expression.type!!)
    }

    override fun generateCodeWithoutChained(): String {
        expression.generateCode()
        return expression.type!!
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

    override fun generateCode() {

        val receiverType = generateCodeWithoutChained()

        chained?.generateAsChained(receiverType)

    }

    override fun generateCodeWithoutChained() =
        when (token.type) {
            TokenType.THIS -> {
                fileWriter.writeLoad(3, "carga del this")
                containerClass.token.lexeme
            }
            else -> {
                fileWriter.writeDataSectionHeader()
                fileWriter.writeDW("string${symbolTable.strLiteralsCount++}", token.lexeme)
                fileWriter.writeCodeSectionHeader()
                fileWriter.writePush("string${symbolTable.strLiteralsCount - 1}")

                "String"
            }
        }
}

class VariableAccess(
    override var token: Token,
    override var parent: ASTMember,
    var containerClass: Class,
    var containerCallable: Callable,
    var containerBlock: Block
): Chained {
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
        if (receiverType == "void" || receiverType in primitiveTypesSet)
            throw InvalidChainingException(token)

        if (token.lexeme !in symbolTable.classMap[receiverType]!!.attributeMap)
            throw InvalidAttributeAccessException(token, receiverType!!)

        val type = symbolTable.classMap[receiverType]!!.attributeMap[token.lexeme]!!.first().typeToken.lexeme

        return chained?.checkChained(type) ?: type
    }

    override fun generateCode() {
        val receiverType = generateCodeWithoutChained()

        chained?.generateAsChained(receiverType)
    }

    override fun generateCodeWithoutChained() =
        when (token.lexeme) {
            in containerBlock.visibleVariablesMap -> {
                val position = -containerBlock.visibleVariablesMap.keys.indexOf(token.lexeme)

                fileWriter.writeLoad(position)

                containerBlock.visibleVariablesMap[token.lexeme]!!.type
            }
            in containerCallable.paramMap -> {
                val stackRecordOffset = (containerCallable as? Method)?.let {
                    if (it.modifier.type == TokenType.STATIC) 2
                    else 3
                } ?: 3

                val position = containerCallable.paramMap.keys.indexOf(token.lexeme) + stackRecordOffset + 1

                fileWriter.writeLoad(position)

                containerCallable.paramMap[token.lexeme]!!.typeToken.lexeme
            }
            else -> {
                val matchingNameAttributeSet = containerClass.attributeMap[token.lexeme]!!

                //obtener el que sea de esta clase o la última redefinición del atributo del mismo nombre
                val attribute = matchingNameAttributeSet.first()

                val offset = attribute.offsetInCIR

                fileWriter.writeLoad(3)
                fileWriter.writeLoadRef(offset)

                attribute.typeToken.lexeme
            }
        }

    override fun generateCodeWithoutChained(receiverType: String): String {
        val matchingNameAttributeSet = symbolTable.classMap[receiverType]!!.attributeMap[token.lexeme]!!

        val attribute = matchingNameAttributeSet.first()

        val offset = attribute.offsetInCIR

        fileWriter.writeLoad(3)
        fileWriter.writeLoadRef(offset)

        return attribute.typeToken.lexeme
    }

    override fun generateAsChained(receiverType: String) {
        //la diferencia acá es que no tiene que cargar this porque la referencia ya está puesta, solo tiene que
        //hacer el loadRef y generar el encadenado
        val ownerClass = symbolTable.classMap[receiverType]

        //obtener el que sea de esta clase o la última redefinición del atributo del mismo nombre
        val attribute = ownerClass!!.attributeMap[token.lexeme]!!.first()

        val offset = attribute.offsetInCIR

        fileWriter.writeLoadRef(offset)

        chained?.generateAsChained(attribute.typeToken.lexeme)
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
        if (receiverType == null || receiverType == "void" || receiverType in primitiveTypesSet)
            throw InvalidChainingException(token)

        if (token.lexeme !in symbolTable.classMap[receiverType]!!.methodMap)
            throw InvalidCallException(
                token,
                "No se puede resolver el método al que se quiere acceder"
            )

        val methodCalled = symbolTable.classMap[receiverType]!!.methodMap[token.lexeme]!!

        if (methodCalled.modifier.lexeme == "static" && receiverType !in containerClass.ancestors)
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

    override fun generateCode() {
        val receiverType = generateCodeWithoutChained()

        chained?.generateAsChained(receiverType)
    }

    override fun generateAsChained(receiverType: String) {
        val receiverType = generateCodeWithoutChained(receiverType)

        chained?.generateAsChained(receiverType)
    }

    override fun generateCodeWithoutChained(): String {
        val calledMethod = containerClass.methodMap[token.lexeme]!!

        if (calledMethod.typeToken.type != TokenType.VOID) {
            fileWriter.writeRMEM(1)
        }

        if (calledMethod.modifier.type == TokenType.STATIC)
            arguments.reversed().forEach { it.generateCode() }
        else {
            fileWriter.writeLoad(3)

            arguments.reversed().forEach { it.generateCodeAsInstanceMetParams() }
        }

        fileWriter.writePush(calledMethod.getCodeLabel())
        fileWriter.writeCall()

        return calledMethod.typeToken.lexeme
    }

    override fun generateCodeWithoutChained(receiverType: String): String {
        val calledMethod = symbolTable.classMap[receiverType]!!.methodMap[token.lexeme]!!

        if (calledMethod.typeToken.type != TokenType.VOID) {
            fileWriter.writeRMEM(1)
            fileWriter.writeSwap()
        }

        if (calledMethod.modifier.type == TokenType.STATIC) {
            arguments.reversed().forEach { it.generateCode() }

            fileWriter.writePush(calledMethod.getCodeLabel())
        }else {
            arguments.reversed().forEach { it.generateCodeAsInstanceMetParams() }

            fileWriter.writeDup()

            //acá tengo que acceder desde la vtable porque no sé si la implementación que es accesible desde el
            //tipo de la variable es la misma que la del tipo dinámico
            fileWriter.writeLoadRef(0, "acceso a vtable")
            fileWriter.writeLoadRef(calledMethod.offsetInVTable, "${calledMethod.token}")
        }

        fileWriter.writeCall()

        return calledMethod.typeToken.lexeme
    }
}

class ConstructorCall(
    override var token: Token,
    override var parent: ASTMember
): Call {
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

    override fun generateCode() {
        generateCodeWithoutChained()

        chained?.generateAsChained(token.lexeme)
    }

    override fun generateCodeWithoutChained(): String {
        val constructorClass = symbolTable.classMap[token.lexeme]!!
        val calledConstructor = constructorClass.constructor
        val cirSize = constructorClass.attributeMap.size + 1

        //this
        fileWriter.writeRMEM(1)
        fileWriter.writePush(cirSize.toString())
        fileWriter.writePush("simple_malloc")
        fileWriter.writeCall()                  //me devuelve la referencia al cir
        fileWriter.write("DUP")
        //guardo en el cir (consumiendo la ref duplicada) la referencia a la vtable
        fileWriter.writePush("vt${token.lexeme}")
        fileWriter.writeStoreRef(0)
        //vuelvo a duplicar la referencia al cir
        fileWriter.write("DUP")

        arguments.reversed().forEach { it.generateCodeAsInstanceMetParams() }

        fileWriter.writePush(calledConstructor.getCodeLabel())
        fileWriter.writeCall()

        return token.lexeme
    }
}

class StaticMethodCall(
    override var parent: ASTMember,
    override var token: Token,
    var calledMethodToken: Token
): Call {

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

    override fun generateCode() {
        val receiverType = generateCodeWithoutChained()

        chained?.generateAsChained(receiverType)
    }

    override fun generateCodeWithoutChained(): String {
        val calledMethod = symbolTable.classMap[token.lexeme]!!.methodMap[calledMethodToken.lexeme]!!

        if (calledMethod.typeToken.type != TokenType.VOID) {
            fileWriter.writeRMEM(1)
            fileWriter.writeSwap()
        }

        arguments.reversed().forEach { it.generateCode()}

        fileWriter.writePush(calledMethod.getCodeLabel())
        fileWriter.writeCall()

        return calledMethod.typeToken.lexeme
    }
}

fun checkCompatibleTypes(expectedType: String?, actualType: String?, token: Token) {
    if (expectedType != null) {
        if (expectedType in primitiveTypesSet) {
            if (actualType == null)
                throw UnexpectedNullOperandException(token, expectedType)

            if (actualType !in primitiveTypesSet)
                throw TypeMismatchException(token, actualType, expectedType)

            if (actualType != expectedType)
                throw TypeMismatchException(token, actualType, expectedType)
        } else {
            if (actualType != null) {
                if (actualType in primitiveTypesSet)
                    throw TypeMismatchException(token, actualType, expectedType)

                if (expectedType !in symbolTable.classMap[actualType]!!.ancestors)
                    throw TypeMismatchException(token, actualType, expectedType)
            }
        }
    }
}