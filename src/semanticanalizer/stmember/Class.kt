package semanticanalizer.stmember

import symbolTable
import utils.Token
import utils.Token.DummyToken
import utils.TokenType.BOOLEAN
import utils.TokenType.CHAR
import utils.TokenType.CLASS_IDENTIFIER
import utils.TokenType.FINAL
import utils.TokenType.INT
import utils.TokenType.MET_VAR_IDENTIFIER
import utils.TokenType.STATIC
import utils.TokenType.VOID
import utils.TokenType.ABSTRACT
import java.util.Stack

private val objectToken = Token(CLASS_IDENTIFIER, "Object", -1)

open class Class() : Modifiable {

    private companion object {
        var withoutCircularity = mutableSetOf<Class>()
        var mightOrDoHaveCircularity = mutableSetOf<Class>()
    }

    override var token: Token = DummyToken
    override var modifier: Token = DummyToken
    override var declarationCompleted = false

    var constructor: Constructor = Constructor(parentClass = this)
    var parentClassToken: Token = objectToken
    var methodMap = mutableMapOf<String, Method>()
    val attributeMap = mutableMapOf<String, MutableSet<Attribute>>()

    var parentClass: Class = Object

    var isConsolidated = false

    val ancestors = mutableSetOf(objectToken.lexeme)

    fun consolidate() {
        parentClass = symbolTable.classMap[parentClassToken.lexeme]!!

        if (modifier.type == ABSTRACT && parentClass != Object && parentClass.modifier.type != ABSTRACT)
            throw InvalidInheritanceException(
                "las clases abstractas no pueden heredar de clases concretas",
                token
            )

        if (isConsolidated)
            return

        if (parentClass.isConsolidated.not())
            parentClass.consolidate()

        parentClass.attributeMap.forEach { (key, value) ->
            attributeMap.getOrPut(key) { mutableSetOf() }.addAll(value)
        }

        //métodos
        val metIntersection = mutableSetOf<Method>()

        methodMap.values.forEach {
            if (it.token.lexeme in parentClass.methodMap.keys) {
                metIntersection.add(it)
            }
        }


        metIntersection.forEach {
            val parentMethodDefinition = parentClass.methodMap[it.token.lexeme]!!

            if (parentMethodDefinition.modifier.type == FINAL || parentMethodDefinition.modifier.type == STATIC)
                throw InvalidRedefinitionException(
                    "no se admite la redefinición de métodos final ni static",
                    it.token
                )

            if (parentMethodDefinition.modifier.type == ABSTRACT) {
                if (it.modifier.type == ABSTRACT)
                    throw InvalidRedefinitionException(
                        "los métodos abstractos redefinidos no pueden ser también abstractos",
                        it.modifier
                    )
            }
            checkValidRedefinition(it, parentMethodDefinition)
        }

        val methodsToAdd = parentClass.methodMap - metIntersection.map { it.token.lexeme }.toSet()

        methodsToAdd.forEach { (_,value) ->
            if (value.modifier.type == ABSTRACT && modifier.type != ABSTRACT)
                throw InvalidClassDeclarationException(
                    "la clase es concreta y no implementa todos los métodos abstractos",
                    token
                )
        }

        methodMap.putAll(methodsToAdd)

        isConsolidated = true
    }

    private fun checkValidRedefinition(currentMethodDefinition: Method, parentMethodDefinition: Method) {

        if (currentMethodDefinition.modifier.type == STATIC)
            throw InvalidRedefinitionException(
                "La redefinición de un método no puede tener modificador static",
                currentMethodDefinition.token
            )



        if (currentMethodDefinition.paramMap.size != parentMethodDefinition.paramMap.size)
            throw InvalidRedefinitionException(
                "La redefinición no tiene la misma cantidad de parámetros",
                currentMethodDefinition.token
            )

        if (currentMethodDefinition.typeToken.type != parentMethodDefinition.typeToken.type)
            throw InvalidRedefinitionException(
                "El tipo de retorno de la redefinición no coincide con el tipo de retorno del padre.",
                currentMethodDefinition.token
            )

        val currentParamsCollection = currentMethodDefinition.paramMap.keys
        val parentParamsCollection = parentMethodDefinition.paramMap.keys

        currentMethodDefinition.paramMap.forEach { (key, value) ->
            if (currentParamsCollection.indexOf(key) != parentParamsCollection.indexOf(key))
                throw InvalidRedefinitionException(
                    "El orden de los parámetros no coincide en la redefinición",
                    currentMethodDefinition.token
                )

            if (value.typeToken.type != parentMethodDefinition.paramMap[key]!!.typeToken.type)
                throw InvalidRedefinitionException(
                    "El tipo de los parámetros no coincide en la redefinición",
                    value.typeToken
                )
            else if (value.typeToken.type == CLASS_IDENTIFIER && value.typeToken.lexeme != parentMethodDefinition.paramMap[key]!!.typeToken.lexeme) {
                throw InvalidRedefinitionException(
                    "El tipo de los parámetros no coincide en la redefinición",
                    value.typeToken
                )
            }
        }
    }

    override fun isWellDeclared() {
        if (this == Object || this == System || this == StringClass)
            return

        val parentClass = symbolTable.classMap[parentClassToken.lexeme]
            ?: throw UndeclaredClassException(
                "La clase padre $parentClassToken no está declarada",
                parentClassToken
            )

        if (parentClass.modifier.type == STATIC || parentClass.modifier.type == FINAL)
            throw InvalidInheritanceException(
                "No se puede heredar de clases static o final",
                parentClassToken
            )

        checkCircularInheritance()

        constructor.isWellDeclared()

        attributeMap.values.forEach { set ->
            set.forEach { attr ->
                attr.isWellDeclared()
            }
        }

        methodMap.values.forEach {
            it.isWellDeclared()
        }
    }

    private fun checkCircularInheritance() {
        //recordá que usaste una pila en lugar de una única var por si
        //llegás a implementar el logro de interfaces, que permiten herencia múltiple
        //cambiaría muy poco en tal caso (tendrías que guardar el camino inválido
        //para no invalidar ramas válidas de herencia
        val border = Stack<Class>()
        withoutCircularity.addAll(setOf(Object, StringClass, System))

        ancestors.add(token.lexeme)
        border.push(this)

        while (border.isNotEmpty()) {
            val current = border.pop()

            if (current in withoutCircularity) {
                ancestors.addAll(current.ancestors)
                break
            }

            if (current in mightOrDoHaveCircularity)
                throw CircularInheritanceException(
                    "Herencia circular detectada",
                    current.token
                )

            mightOrDoHaveCircularity.add(current)

            if (current.hasExplicitParent()) {
                val parentClass = symbolTable.classMap[current.parentClassToken.lexeme]
                    ?: throw UndeclaredClassException(
                        "La clase padre ${current.parentClassToken} no está declarada",
                        current.parentClassToken
                    )

                ancestors.add(parentClass.token.lexeme)
                border.push(parentClass)
            }

        }

        withoutCircularity.addAll(mightOrDoHaveCircularity)
        mightOrDoHaveCircularity.removeAll { true }
    }

    private fun hasExplicitParent() = parentClassToken != objectToken

    fun owns(method: Method): Boolean {
        return method.parentClass === this
    }

    override fun toString(): String {
        var strRep = "$modifier $token.lexeme extends ${parentClassToken}"

        strRep += "- Constructor: $constructor"

        strRep += "- Métodos:\n"
        methodMap.forEach {
            strRep += "---- $it\n"
        }

        strRep += "- Atributos:\n"
        attributeMap.forEach {
            strRep += "---- $it\n"
        }

        return strRep
    }
}

object DummyClass: Class()

object Object: Class() {
    override var token: Token = objectToken

    init {
        isConsolidated = true

        methodMap = mutableMapOf(
            "debugPrint" to Method(
                Token(MET_VAR_IDENTIFIER, "debugPrint", -1),
                this
            ).also {
                it.modifier = Token(STATIC, "static", -1)
                it.typeToken = Token(VOID, "void", -1)
                it.paramMap = mutableMapOf(
                    "i" to FormalArgument(
                        Token(MET_VAR_IDENTIFIER, "i", -1),
                        Token(INT, "int", -1),
                        it
                    )
                )
            }
        )
    }
}

object StringClass: Class() {
    override var token: Token = Token(CLASS_IDENTIFIER, "String", -1)

    init {
        parentClass = Object
        isConsolidated = true
        ancestors += "String"
    }
}

object System : Class() {
    override var token: Token = Token(CLASS_IDENTIFIER, "System", -1)

    private val staticToken = Token(STATIC, "static", -1)
    private val intToken = Token(INT, "int", -1)

    init {
        parentClass = Object
        isConsolidated = true

        methodMap = mutableMapOf(
            "read" to Method(
                Token(MET_VAR_IDENTIFIER, "read", -1),
                this
            ).also {
                it.typeToken = intToken
                it.modifier = staticToken
            },
            "printB" to Method(
                Token(MET_VAR_IDENTIFIER, "printB", -1),
                this
            ).also {
                it.typeToken = Token(VOID, "void", -1)
                it.modifier = staticToken
                it.paramMap = mutableMapOf(
                    "b" to FormalArgument(
                        Token(MET_VAR_IDENTIFIER, "b", -1),
                        Token(BOOLEAN, "boolean", -1),
                        it
                    )
                )
            },
            "printC" to Method(
                Token(MET_VAR_IDENTIFIER, "printC", -1),
                this
            ).also {
                it.typeToken = Token(VOID, "void", -1)
                it.modifier = staticToken
                it.paramMap = mutableMapOf(
                    "c" to FormalArgument(
                        Token(MET_VAR_IDENTIFIER, "c", -1),
                        Token(CHAR, "char", -1),
                        it
                    )
                )
            },
            "printI" to Method(
                Token(MET_VAR_IDENTIFIER, "printI", -1),
                this
            ).also {
                it.typeToken = Token(VOID, "void", -1)
                it.modifier = staticToken
                it.paramMap = mutableMapOf(
                    "i" to FormalArgument(
                        Token(MET_VAR_IDENTIFIER, "i", -1),
                        intToken,
                        it
                    )
                )
            },
            "printS" to Method(
                Token(MET_VAR_IDENTIFIER, "printS", -1),
                this
            ).also {
                it.typeToken = Token(VOID, "void", -1)
                it.modifier = staticToken
                it.paramMap = mutableMapOf(
                    "s" to FormalArgument(
                        Token(MET_VAR_IDENTIFIER, "s", -1),
                        Token(CLASS_IDENTIFIER, "String", -1),
                        it
                    )
                )
            },
            "println" to Method(
                Token(MET_VAR_IDENTIFIER, "println", -1),
                this
            ).also {
                it.typeToken = Token(VOID, "void", -1)
                it.modifier = staticToken
            },
            "printBln" to Method(
                Token(MET_VAR_IDENTIFIER, "printBln", -1),
                this
            ).also {
                it.typeToken = Token(VOID, "void", -1)
                it.modifier = staticToken
                it.paramMap = mutableMapOf(
                    "b" to FormalArgument(
                        Token(MET_VAR_IDENTIFIER, "b", -1),
                        Token(BOOLEAN, "boolean", -1),
                        it
                    )
                )
            },
            "printCln" to Method(
                Token(MET_VAR_IDENTIFIER, "printCln", -1),
                this
            ).also {
                it.typeToken = Token(VOID, "void", -1)
                it.modifier = staticToken
                it.paramMap = mutableMapOf(
                    "c" to FormalArgument(
                        Token(MET_VAR_IDENTIFIER, "c", -1),
                        Token(CHAR, "char", -1),
                        it
                    )
                )
            },
            "printIln" to Method(
                Token(MET_VAR_IDENTIFIER, "printIln", -1),
                this
            ).also {
                it.typeToken = Token(VOID, "void", -1)
                it.modifier = staticToken
                it.paramMap = mutableMapOf(
                    "i" to FormalArgument(
                        Token(MET_VAR_IDENTIFIER, "i", -1),
                        intToken,
                        it
                    )
                )
            },
            "printSln" to Method(
                Token(MET_VAR_IDENTIFIER, "printSln", -1),
                this
            ).also {
                it.typeToken = Token(VOID, "void", -1)
                it.modifier = staticToken
                it.paramMap = mutableMapOf(
                    "s" to FormalArgument(
                        Token(MET_VAR_IDENTIFIER, "s", -1),
                        Token(CLASS_IDENTIFIER, "String", -1),
                        it
                    )
                )
            }
        )

        ancestors += "System"
    }
}