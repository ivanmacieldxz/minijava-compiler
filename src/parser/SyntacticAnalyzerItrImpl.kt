package parser

import lexer.LexicalAnalyzer
import semanticanalizer.stmember.Attribute
import semanticanalizer.stmember.BadlyNamedConstructorException
import semanticanalizer.stmember.Callable
import semanticanalizer.stmember.CircularInheritanceException
import semanticanalizer.stmember.Class
import semanticanalizer.stmember.Constructor
import semanticanalizer.stmember.Declarable
import semanticanalizer.stmember.DummyClass
import semanticanalizer.stmember.DummyContext
import semanticanalizer.stmember.FormalArgument
import semanticanalizer.stmember.InvalidClassNameException
import semanticanalizer.stmember.InvalidConstructorDeclarationException
import semanticanalizer.stmember.InvalidMethodDeclarationException
import semanticanalizer.stmember.Method
import semanticanalizer.stmember.MoreThanOneConstructorDeclarationException
import semanticanalizer.stmember.Object
import semanticanalizer.stmember.RepeatedDeclarationException
import semanticanalizer.SymbolTable.Predefined
import semanticanalizer.ast.ASTBuilder
import semanticanalizer.ast.ASTMember
import semanticanalizer.ast.member.Assignment
import semanticanalizer.ast.member.BasicExpression
import semanticanalizer.ast.member.BinaryExpression
import semanticanalizer.ast.member.BinaryOperator
import semanticanalizer.ast.member.Block
import semanticanalizer.ast.member.CompoundSentence
import semanticanalizer.ast.member.ConstructorCall
import semanticanalizer.ast.member.Else
import semanticanalizer.ast.member.Expression
import semanticanalizer.ast.member.If
import semanticanalizer.ast.member.LiteralPrimary
import semanticanalizer.ast.member.LocalVar
import semanticanalizer.ast.member.ParenthesizedExpression
import semanticanalizer.ast.member.Primitive
import semanticanalizer.ast.member.Return
import semanticanalizer.ast.member.Sentence
import semanticanalizer.ast.member.UnaryOperator
import semanticanalizer.ast.member.While
import symbolTable
import utils.NonTerminal
import utils.NonTerminal.Companion.follow
import utils.SyntacticStackable
import utils.Token
import utils.TokenType
import kotlin.collections.setOf

class SyntacticAnalyzerItrImpl(
    private val lexer: LexicalAnalyzer
): SyntacticAnalyzer {
    private var expectedElementsStack = ArrayDeque<SyntacticStackable>()
    private lateinit var currentToken: Token

    private var astBuilder = ASTBuilder()

    override fun start() {

        expectedElementsStack.addFirst(NonTerminal.INITIAL)
        currentToken = lexer.getNextToken()
        var currentStackElement = expectedElementsStack.removeFirst()

        while (currentStackElement != TokenType.EOF) {
            when (currentStackElement) {
                is NonTerminal -> {
                    when (currentStackElement) {
                        NonTerminal.INITIAL -> {
                            expectedElementsStack.addFirst(TokenType.EOF)
                            expectedElementsStack.addFirst(NonTerminal.CLASS_LIST)
                        }

                        NonTerminal.CLASS_LIST -> {
                            if (currentToken.inFirsts(currentStackElement)) {
                                expectedElementsStack.addFirst(NonTerminal.CLASS_LIST)
                                expectedElementsStack.addFirst(NonTerminal.CLASS)
                            } else if (currentToken.inNexts(currentStackElement).not()) {
                                throwUnexpectedTerminalException(currentStackElement)
                            }
                        }

                        NonTerminal.CLASS -> {
                            expectedElementsStack.addFirst(TokenType.RIGHT_CURLY_BRACKET)
                            expectedElementsStack.addFirst(NonTerminal.MEMBER_LIST)
                            expectedElementsStack.addFirst(TokenType.LEFT_CURLY_BRACKET)
                            expectedElementsStack.addFirst(NonTerminal.OPTIONAL_INHERITANCE)
                            expectedElementsStack.addFirst(TokenType.CLASS_IDENTIFIER)
                            expectedElementsStack.addFirst(TokenType.CLASS)
                            expectedElementsStack.addFirst(NonTerminal.OPTIONAL_MODIFIER)

                            symbolTable.currentClass = Class()
                            symbolTable.currentContext = symbolTable.currentClass
                        }

                        NonTerminal.OPTIONAL_MODIFIER -> {
                            if (currentToken.inFirsts(currentStackElement)) {
                                expectedElementsStack.addFirst(NonTerminal.MODIFIER)
                            } else if (currentToken.inNexts(currentStackElement).not()) {
                                throwUnexpectedTerminalException(currentStackElement)
                            }
                        }

                        NonTerminal.MODIFIER -> {
                            symbolTable.accumulator.modifier = currentToken

                            matchAnyInFirst(currentStackElement)
                        }

                        NonTerminal.OPTIONAL_INHERITANCE -> {
                            symbolTable.accumulator.foundInheritance = true

                            if (currentToken.inFirsts(currentStackElement)) {
                                expectedElementsStack.addFirst(TokenType.CLASS_IDENTIFIER)
                                expectedElementsStack.addFirst(TokenType.EXTENDS)
                            }
                        }

                        NonTerminal.MEMBER_LIST -> {
                            if (currentToken.inFirsts(currentStackElement)) {
                                expectedElementsStack.addFirst(NonTerminal.MEMBER_LIST)
                                expectedElementsStack.addFirst(NonTerminal.MEMBER)
                            } else if (currentToken.inNexts(currentStackElement).not()) {
                                throwUnexpectedTerminalException(currentStackElement)
                            }
                        }

                        NonTerminal.MEMBER -> {
                            if (currentToken.inFirsts(NonTerminal.TYPE)) {
                                expectedElementsStack.addFirst(NonTerminal.REST_OF_MEMBER_DECLARATION)
                                expectedElementsStack.addFirst(NonTerminal.TYPE)
                            } else if (currentToken.inFirsts(NonTerminal.CONSTRUCTOR)) {
                                expectedElementsStack.addFirst(NonTerminal.CONSTRUCTOR)
                            } else if (currentToken.inFirsts(NonTerminal.MODIFIER)) {
                                expectedElementsStack.addFirst(NonTerminal.REST_OF_METHOD_DECLARATION)
                                expectedElementsStack.addFirst(TokenType.MET_VAR_IDENTIFIER)
                                expectedElementsStack.addFirst(NonTerminal.METHOD_TYPE)
                                expectedElementsStack.addFirst(NonTerminal.MODIFIER)

                                symbolTable.currentContext = Method(
                                    parentClass = symbolTable.currentClass
                                ).apply {
                                    this.modifier = currentToken
                                }
                            } else {
                                expectedElementsStack.addFirst(NonTerminal.REST_OF_METHOD_DECLARATION)
                                expectedElementsStack.addFirst(TokenType.MET_VAR_IDENTIFIER)
                                expectedElementsStack.addFirst(TokenType.VOID)

                                symbolTable.currentContext = Method(
                                    parentClass = symbolTable.currentClass
                                ).apply {
                                    this.typeToken = currentToken
                                }
                            }
                        }

                        NonTerminal.TYPE -> {
                            symbolTable.accumulator.memberType = currentToken

                            matchAnyInFirst(currentStackElement)
                        }

                        NonTerminal.PRIMITIVE_TYPE -> {
                            matchAnyInFirst(currentStackElement)
                        }

                        NonTerminal.REST_OF_MEMBER_DECLARATION -> {
                            expectedElementsStack.addFirst(NonTerminal.END_OF_MEMBER_DECLARATION)
                            expectedElementsStack.addFirst(TokenType.MET_VAR_IDENTIFIER)
                        }

                        NonTerminal.END_OF_MEMBER_DECLARATION -> {
                            if (currentToken.inFirsts(NonTerminal.REST_OF_METHOD_DECLARATION)) {
                                expectedElementsStack.addFirst(NonTerminal.REST_OF_METHOD_DECLARATION)
                            } else {
                                expectedElementsStack.addFirst(TokenType.SEMICOLON)
                            }
                        }

                        NonTerminal.REST_OF_METHOD_DECLARATION -> {
                            expectedElementsStack.addFirst(NonTerminal.OPTIONAL_BLOCK)
                            expectedElementsStack.addFirst(NonTerminal.FORMAL_ARGUMENTS)
                        }

                        NonTerminal.METHOD_TYPE -> {
                            if (currentToken.inFirsts(NonTerminal.TYPE)) {
                                expectedElementsStack.addFirst(NonTerminal.TYPE)
                            } else {
                                expectedElementsStack.addFirst(TokenType.VOID)
                            }

                            (symbolTable.currentContext as Method).typeToken = currentToken
                        }

                        NonTerminal.CONSTRUCTOR -> {
                            expectedElementsStack.addFirst(NonTerminal.BLOCK)
                            expectedElementsStack.addFirst(NonTerminal.FORMAL_ARGUMENTS)
                            expectedElementsStack.addFirst(TokenType.CLASS_IDENTIFIER)
                            expectedElementsStack.addFirst(TokenType.PUBLIC)


                            symbolTable.currentContext = Constructor(parentClass = symbolTable.currentClass)
                        }

                        NonTerminal.FORMAL_ARGUMENTS -> {
                            expectedElementsStack.addFirst(TokenType.RIGHT_BRACKET)
                            expectedElementsStack.addFirst(NonTerminal.OPTIONAL_FORMAL_ARGUMENTS_LIST)
                            expectedElementsStack.addFirst(TokenType.LEFT_BRACKET)
                        }

                        NonTerminal.OPTIONAL_FORMAL_ARGUMENTS_LIST -> {
                            if (currentToken.inFirsts(currentStackElement)) {
                                expectedElementsStack.addFirst(NonTerminal.FORMAL_ARGUMENTS_LIST)
                            } else if (currentToken.inNexts(currentStackElement).not()) {
                                throwUnexpectedTerminalException(currentStackElement)
                            }
                        }

                        NonTerminal.FORMAL_ARGUMENTS_LIST -> {
                            expectedElementsStack.addFirst(NonTerminal.REST_OF_FORMAL_ARGUMENTS_LIST)
                            expectedElementsStack.addFirst(NonTerminal.FORMAL_ARGUMENT)
                        }

                        NonTerminal.REST_OF_FORMAL_ARGUMENTS_LIST -> {
                            if (currentToken.inFirsts(currentStackElement)) {
                                expectedElementsStack.addFirst(NonTerminal.FORMAL_ARGUMENTS_LIST)
                                expectedElementsStack.addFirst(TokenType.COMMA)
                            } else if (currentToken.inNexts(currentStackElement).not()) {
                                throwUnexpectedTerminalException(currentStackElement)
                            }
                        }

                        NonTerminal.FORMAL_ARGUMENT -> {
                            symbolTable.currentContext = FormalArgument(
                                member = symbolTable.currentContext as Callable
                            )

                            expectedElementsStack.addFirst(TokenType.MET_VAR_IDENTIFIER)
                            expectedElementsStack.addFirst(NonTerminal.TYPE)
                        }

                        NonTerminal.OPTIONAL_BLOCK -> {
                            if (currentToken.inFirsts(NonTerminal.BLOCK)) {
                                expectedElementsStack.addFirst(NonTerminal.BLOCK)
                            } else {
                                expectedElementsStack.addFirst(TokenType.SEMICOLON)
                            }
                        }

                        NonTerminal.BLOCK -> {
                            expectedElementsStack.addFirst(TokenType.RIGHT_CURLY_BRACKET)
                            expectedElementsStack.addFirst(NonTerminal.SENTENCE_LIST)
                            expectedElementsStack.addFirst(TokenType.LEFT_CURLY_BRACKET)
                        }

                        NonTerminal.SENTENCE_LIST -> {
                            if (currentToken.inFirsts(currentStackElement)) {
                                expectedElementsStack.addFirst(NonTerminal.SENTENCE_LIST)
                                expectedElementsStack.addFirst(NonTerminal.SENTENCE)
                            } else if (currentToken.inNexts(currentStackElement).not()) {
                                throwUnexpectedTerminalException(currentStackElement)
                            }
                        }

                        NonTerminal.SENTENCE -> {
                            if (currentToken.inFirsts(NonTerminal.EXPRESSION)) {
                                expectedElementsStack.addFirst(TokenType.SEMICOLON)
                                expectedElementsStack.addFirst(NonTerminal.EXPRESSION)
                            } else if (currentToken.inFirsts(NonTerminal.LOCAL_VARIABLE)) {
                                expectedElementsStack.addFirst(TokenType.SEMICOLON)
                                expectedElementsStack.addFirst(NonTerminal.LOCAL_VARIABLE)
                            } else if (currentToken.inFirsts(NonTerminal.RETURN)) {
                                expectedElementsStack.addFirst(TokenType.SEMICOLON)
                                expectedElementsStack.addFirst(NonTerminal.RETURN)
                            } else if (currentToken.inFirsts(NonTerminal.IF)) {
                                expectedElementsStack.addFirst(NonTerminal.IF)
                            } else if (currentToken.inFirsts(NonTerminal.WHILE)) {
                                expectedElementsStack.addFirst(NonTerminal.WHILE)
                            } else if (currentToken.inFirsts(NonTerminal.BLOCK)) {
                                expectedElementsStack.addFirst(NonTerminal.BLOCK)
                            } else {
                                expectedElementsStack.addFirst(TokenType.SEMICOLON)
                            }
                        }

                        NonTerminal.LOCAL_VARIABLE -> {
                            expectedElementsStack.addFirst(NonTerminal.COMPOUND_EXPRESSION)
                            expectedElementsStack.addFirst(TokenType.ASSIGNMENT)
                            expectedElementsStack.addFirst(TokenType.MET_VAR_IDENTIFIER)

                            expectedElementsStack.addFirst(TokenType.VAR)

                            astBuilder.currentContext = LocalVar(
                                parentMember = symbolTable.currentContext as Callable,
                                token = currentToken,
                                parentSentence = astBuilder.currentContext as Sentence
                            ).also {
                                when (val parent = it.parentSentence) {
                                    is Block -> {
                                        parent.childrenList.add(it)
                                    }
                                    is CompoundSentence -> {
                                        throw Exception("No se admiten declaraciones de variables como única sentencia dentro de un if, else o while")
                                    }
                                }
                            }

                        }

                        NonTerminal.RETURN -> {
                            expectedElementsStack.addFirst(NonTerminal.OPTIONAL_EXPRESSION)
                            expectedElementsStack.addFirst(TokenType.RETURN)
                        }

                        NonTerminal.OPTIONAL_EXPRESSION -> {
                            if (currentToken.inFirsts(NonTerminal.EXPRESSION)) {
                                expectedElementsStack.addFirst(NonTerminal.EXPRESSION)
                            } else if (currentToken.inNexts(currentStackElement).not()) {
                                throwUnexpectedTerminalException(currentStackElement)
                            }
                        }

                        NonTerminal.IF -> {
                            expectedElementsStack.addFirst(NonTerminal.OPTIONAL_ELSE)
                            expectedElementsStack.addFirst(NonTerminal.SENTENCE)
                            expectedElementsStack.addFirst(TokenType.RIGHT_BRACKET)
                            expectedElementsStack.addFirst(NonTerminal.EXPRESSION)
                            expectedElementsStack.addFirst(TokenType.LEFT_BRACKET)
                            expectedElementsStack.addFirst(TokenType.IF)
                        }

                        NonTerminal.OPTIONAL_ELSE -> {
                            if (currentToken.inFirsts(currentStackElement)) {
                                expectedElementsStack.addFirst(NonTerminal.SENTENCE)
                                expectedElementsStack.addFirst(TokenType.ELSE)
                            } else if (currentToken.inNexts(currentStackElement).not()) {
                                throwUnexpectedTerminalException(currentStackElement)
                            }
                        }

                        NonTerminal.WHILE -> {
                            expectedElementsStack.addFirst(NonTerminal.SENTENCE)
                            expectedElementsStack.addFirst(TokenType.RIGHT_BRACKET)
                            expectedElementsStack.addFirst(NonTerminal.EXPRESSION)
                            expectedElementsStack.addFirst(TokenType.LEFT_BRACKET)
                            expectedElementsStack.addFirst(TokenType.WHILE)
                        }

                        NonTerminal.EXPRESSION -> {
                            expectedElementsStack.addFirst(NonTerminal.REST_OF_EXPRESSION)
                            expectedElementsStack.addFirst(NonTerminal.COMPOUND_EXPRESSION)
                        }

                        NonTerminal.REST_OF_EXPRESSION -> {
                            if (currentToken.inFirsts(currentStackElement)) {
                                expectedElementsStack.addFirst(NonTerminal.COMPOUND_EXPRESSION)
                                expectedElementsStack.addFirst(NonTerminal.ASSIGNMENT_OPERATOR)
                            } else if (currentToken.inNexts(currentStackElement).not()) {
                                throwUnexpectedTerminalException(currentStackElement)
                            }
                        }

                        NonTerminal.ASSIGNMENT_OPERATOR -> {
                            val leftExpression = when (val parent = astBuilder.currentContext) {
                                is Block -> {
                                    parent.childrenList.last()
                                }
                                is If -> {
                                    if (parent.body == null)
                                        parent.condition
                                    else
                                        parent.body
                                }
                                is While -> {
                                    if (parent.body == null)
                                        parent.condition
                                    else
                                        parent.body
                                }
                                is Else -> {
                                    parent.body
                                }
                                is Return -> {
                                    parent.body
                                }
                                else -> {}
                            }

                            astBuilder.currentContext = Assignment(
                                astBuilder.currentContext!!,
                                leftExpression as Expression,
                                currentToken
                            ).also {
                                it.leftExpression.parentNode = it
                                when (val parent = it.parentNode) {
                                    is Block ->  {
                                        parent.childrenList.removeLast()
                                        parent.childrenList.addLast(it)
                                    }
                                    is If -> {
                                        if (parent.condition === it.leftExpression)
                                            parent.condition = it
                                        else
                                            parent.body = it
                                    }
                                    is While -> {
                                        if (parent.condition === it.leftExpression)
                                            parent.condition = it
                                        else
                                            parent.body = it
                                    }
                                    is Else -> {
                                        parent.body = it
                                    }
                                    is Return -> {
                                        parent.body = it
                                    }
                                }
                            }

                            expectedElementsStack.addFirst(TokenType.ASSIGNMENT)
                        }

                        NonTerminal.COMPOUND_EXPRESSION -> {
                            expectedElementsStack.addFirst(NonTerminal.REST_OF_COMPOUND_EXPRESSION)
                            expectedElementsStack.addFirst(NonTerminal.BASIC_EXPRESSION)
                        }

                        NonTerminal.REST_OF_COMPOUND_EXPRESSION -> {
                            if (currentToken.inFirsts(currentStackElement)) {
                                expectedElementsStack.addFirst(NonTerminal.REST_OF_COMPOUND_EXPRESSION)
                                expectedElementsStack.addFirst(NonTerminal.BASIC_EXPRESSION)
                                expectedElementsStack.addFirst(NonTerminal.BINARY_OPERATOR)
                            } else if (currentToken.inNexts(currentStackElement).not()) {
                                throwUnexpectedTerminalException(currentStackElement)
                            } else {
                                astBuilder.currentContext =
                                    (astBuilder.currentContext as BasicExpression).parentNode

                                var astContext = astBuilder.currentContext

                                while (astContext is Expression) {
                                    astContext = astContext.parentNode
                                    astBuilder.currentContext = astContext
                                }
                            }
                        }

                        NonTerminal.BINARY_OPERATOR -> {
                            val basicExpression = (astBuilder.currentContext as BasicExpression)
                            astBuilder.currentContext = BinaryExpression(
                                basicExpression.parentNode,
                                basicExpression,
                                BinaryOperator(currentToken)
                            ).also {
                                it.leftExpression.parentNode = it
                                when (val parent = it.parentNode) {
                                    is Block ->  {
                                        parent.childrenList.removeLast()
                                        parent.childrenList.addLast(it)
                                    }
                                    is If -> {
                                        if (parent.condition === it.leftExpression)
                                            parent.condition = it
                                        else
                                            parent.body = it
                                    }
                                    is While -> {
                                        if (parent.condition === it.leftExpression)
                                            parent.condition = it
                                        else
                                            parent.body = it
                                    }
                                    is Else -> {
                                        parent.body = it
                                    }
                                    is Return -> {
                                        parent.body = it
                                    }
                                    is BinaryExpression -> {
                                        parent.rightExpression = it
                                    }
                                    is Assignment -> {
                                        parent.rightExpression = it
                                    }
                                }
                            }

                            matchAnyInFirst(currentStackElement)
                        }

                        NonTerminal.BASIC_EXPRESSION -> {
                            expectedElementsStack.addFirst(NonTerminal.OPERAND)

                            astBuilder.currentContext = BasicExpression(
                                astBuilder.currentContext!!
                            ).also {
                                when (val parentNode = it.parentNode) {
                                    is Block -> {
                                        parentNode.childrenList.add(it)
                                    }
                                    is If -> {
                                        if (parentNode.condition == null)
                                            parentNode.condition = it
                                        else {
                                            parentNode.body = it
                                        }
                                    }
                                    is Else -> {
                                        if (parentNode.body == null)
                                            parentNode.body = it
                                    }
                                    is While -> {
                                        if (parentNode.condition == null)
                                            parentNode.condition = it
                                        else
                                            parentNode.body = it
                                    }
                                    is Return -> {
                                        parentNode.body = it
                                    }
                                    is ParenthesizedExpression -> {
                                        parentNode.expression = it
                                    }
                                    is LocalVar -> {
                                        parentNode.expression = it
                                    }
                                    is BinaryExpression -> {
                                        parentNode.rightExpression = it
                                    }
                                    is Assignment -> {
                                        parentNode.rightExpression = it
                                    }
                                }
                            }

                            if (currentToken.inFirsts(NonTerminal.UNARY_OPERATOR)) {
                                expectedElementsStack.addFirst(NonTerminal.UNARY_OPERATOR)

                                (astBuilder.currentContext as BasicExpression).operator = UnaryOperator(
                                    currentToken
                                )
                            }
                        }

                        NonTerminal.UNARY_OPERATOR -> {
                            matchAnyInFirst(currentStackElement)
                        }

                        NonTerminal.OPERAND -> {
                            if (currentToken.inFirsts(NonTerminal.PRIMITIVE)) {
                                expectedElementsStack.addFirst(NonTerminal.PRIMITIVE)

                                (astBuilder.currentContext as BasicExpression).operand = Primitive(
                                    token = currentToken
                                )

                            } else {
                                expectedElementsStack.addFirst(NonTerminal.REFERENCE)
                            }
                        }

                        NonTerminal.PRIMITIVE -> {
                            matchAnyInFirst(currentStackElement)
                        }

                        NonTerminal.REFERENCE -> {
                            expectedElementsStack.addFirst(NonTerminal.REST_OF_REFERENCE)
                            expectedElementsStack.addFirst(NonTerminal.PRIMARY)
                        }

                        NonTerminal.REST_OF_REFERENCE -> {
                            if (currentToken.inFirsts(NonTerminal.CHAINED_MET_VAR)) {
                                expectedElementsStack.addFirst(NonTerminal.REST_OF_REFERENCE)
                                expectedElementsStack.addFirst(NonTerminal.CHAINED_MET_VAR)
                            } else if (currentToken.inNexts(currentStackElement).not()) {
                                throwUnexpectedTerminalException(currentStackElement)
                            }
                        }

                        NonTerminal.PRIMARY -> {
                            if (currentToken.inFirsts(NonTerminal.VAR_ACCESS_OR_MET_CALL)) {
                                expectedElementsStack.addFirst(NonTerminal.VAR_ACCESS_OR_MET_CALL)
                            } else if (currentToken.inFirsts(NonTerminal.CONSTRUCTOR_CALL)) {
                                expectedElementsStack.addFirst(NonTerminal.CONSTRUCTOR_CALL)
                            } else if (currentToken.inFirsts(NonTerminal.STATIC_METHOD_CALL)) {
                                expectedElementsStack.addFirst(NonTerminal.STATIC_METHOD_CALL)
                            } else if (currentToken.inFirsts(NonTerminal.PARENTHESIZED_EXPRESSION)) {
                                expectedElementsStack.addFirst(NonTerminal.PARENTHESIZED_EXPRESSION)

                                (astBuilder.currentContext as BasicExpression).apply {
                                    operand = ParenthesizedExpression(
                                        this
                                    )
                                    astBuilder.currentContext = operand
                                }
                            } else {
                                //strLit o this

                                (astBuilder.currentContext as BasicExpression).operand = LiteralPrimary(
                                    currentToken
                                )

                                matchAnyInFirst(currentStackElement)
                            }
                        }

                        NonTerminal.VAR_ACCESS_OR_MET_CALL -> {
                            expectedElementsStack.addFirst(NonTerminal.REST_OF_OPTIONAL_METHOD_CALL)
                            expectedElementsStack.addFirst(TokenType.MET_VAR_IDENTIFIER)
                        }

                        NonTerminal.REST_OF_OPTIONAL_METHOD_CALL -> {
                            if (currentToken.inFirsts(currentStackElement)) {
                                expectedElementsStack.addFirst(NonTerminal.ACTUAL_ARGUMENTS)
                            } else if (currentToken.inNexts(currentStackElement).not()) {
                                throwUnexpectedTerminalException(currentStackElement)
                            } else {
                                //si no está en los primeros, pero sí en los siguientes, entonces era un acceso a var, no a met

                            }
                        }

                        NonTerminal.CONSTRUCTOR_CALL -> {
                            expectedElementsStack.addFirst(NonTerminal.ACTUAL_ARGUMENTS)

                            match(TokenType.NEW)

                            val token = matchAndReturn(TokenType.CLASS_IDENTIFIER)

                            (astBuilder.currentContext as BasicExpression).operand = ConstructorCall(
                                token
                            )
                        }

                        NonTerminal.PARENTHESIZED_EXPRESSION -> {
                            expectedElementsStack.addFirst(TokenType.RIGHT_BRACKET)
                            expectedElementsStack.addFirst(NonTerminal.EXPRESSION)
                            expectedElementsStack.addFirst(TokenType.LEFT_BRACKET)
                        }

                        NonTerminal.STATIC_METHOD_CALL -> {
                            expectedElementsStack.addFirst(NonTerminal.ACTUAL_ARGUMENTS)
                            expectedElementsStack.addFirst(TokenType.MET_VAR_IDENTIFIER)
                            expectedElementsStack.addFirst(TokenType.DOT)
                            expectedElementsStack.addFirst(TokenType.CLASS_IDENTIFIER)
                        }

                        NonTerminal.ACTUAL_ARGUMENTS -> {
                            expectedElementsStack.addFirst(TokenType.RIGHT_BRACKET)
                            expectedElementsStack.addFirst(NonTerminal.OPTIONAL_EXPRESSION_LIST)
                            expectedElementsStack.addFirst(TokenType.LEFT_BRACKET)
                        }

                        NonTerminal.OPTIONAL_EXPRESSION_LIST -> {
                            if (currentToken.inFirsts(currentStackElement)) {
                                expectedElementsStack.addFirst(NonTerminal.EXPRESSION_LIST)
                            } else if (currentToken.inNexts(currentStackElement).not()) {
                                throwUnexpectedTerminalException(currentStackElement)
                            }
                        }

                        NonTerminal.EXPRESSION_LIST -> {
                            expectedElementsStack.addFirst(NonTerminal.REST_OF_EXPRESSION_LIST)
                            expectedElementsStack.addFirst(NonTerminal.EXPRESSION)
                        }

                        NonTerminal.REST_OF_EXPRESSION_LIST -> {
                            if (currentToken.inFirsts(currentStackElement)) {
                                expectedElementsStack.addFirst(NonTerminal.EXPRESSION_LIST)
                                expectedElementsStack.addFirst(TokenType.COMMA)
                            } else if (currentToken.inNexts(currentStackElement).not()) {
                                throwUnexpectedTerminalException(currentStackElement)
                            }
                        }

                        NonTerminal.CHAINED_MET_VAR -> {
                            expectedElementsStack.addFirst(NonTerminal.REST_OF_CHAINING)
                            expectedElementsStack.addFirst(TokenType.MET_VAR_IDENTIFIER)
                            expectedElementsStack.addFirst(TokenType.DOT)
                        }

                        NonTerminal.REST_OF_CHAINING -> {
                            if (currentToken.inFirsts(currentStackElement)) {
                                expectedElementsStack.addFirst(NonTerminal.ACTUAL_ARGUMENTS)
                            } else if (currentToken.inNexts(currentStackElement).not()) {
                                throwUnexpectedTerminalException(currentStackElement)
                            }
                        }

                    }
                }
                is TokenType -> {
                    val prevToken = matchAndReturn(currentStackElement)

                    when (prevToken.type) {
                        TokenType.CLASS_IDENTIFIER -> {
                            when (val currentContext = symbolTable.currentContext) {
                                is Class -> {
                                    when (symbolTable.accumulator.className) {
                                        Token.DummyToken -> {
                                            symbolTable.accumulator.className = prevToken
                                        }
                                        else -> {
                                            if (symbolTable.accumulator.foundInheritance) {
                                                if (symbolTable.accumulator.classParent.lexeme == prevToken.lexeme &&
                                                    prevToken.lexeme != Object.token.lexeme)
                                                    throw CircularInheritanceException(
                                                        "herencia circular detectada.",
                                                        prevToken
                                                    )

                                                symbolTable.accumulator.classParent = prevToken
                                            }
                                        }
                                    }
                                }
                                is Constructor -> {
                                    if (symbolTable.currentClass.modifier.type == TokenType.ABSTRACT)
                                        throw InvalidConstructorDeclarationException(
                                            "las clases abstractas no pueden tener constructores",
                                            prevToken
                                        )

                                    if (symbolTable.currentClass.token.lexeme != prevToken.lexeme)
                                        throw BadlyNamedConstructorException(
                                            "el nombre del constructor no coincide con el de la clase",
                                            prevToken
                                        )

                                    currentContext.token = prevToken
                                }
                                is FormalArgument -> {
                                    currentContext.typeToken = prevToken
                                }
                            }
                        }

                        TokenType.EXTENDS -> {
                            symbolTable.accumulator.foundInheritance = true
                        }

                        TokenType.LEFT_CURLY_BRACKET -> {
                            val currentContext = symbolTable.currentContext

                            if (currentContext.declarationCompleted.not()) {
                                when (currentContext) {
                                    is Class -> {

                                        currentContext.token = symbolTable.accumulator.className
                                        currentContext.modifier = symbolTable.accumulator.modifier
                                        currentContext.parentClass = Object
                                        currentContext.parentClassToken = symbolTable.accumulator.classParent

                                        if (currentContext.token.lexeme in Predefined.classesNames)
                                            throw InvalidClassNameException(
                                                "la clase ${currentContext.token.lexeme} es parte de las clases predefinidas",
                                                currentContext.token
                                            )

                                        symbolTable.classMap.putIfAbsentOrError(
                                            currentContext.token.lexeme,
                                            symbolTable.currentClass
                                        ) {
                                            throw RepeatedDeclarationException(
                                                " la clase ${currentContext.token.lexeme} fue declarada previamente",
                                                currentContext.token
                                            )
                                        }

                                        symbolTable.accumulator.modifier = Token.DummyToken
                                        symbolTable.accumulator.foundInheritance = false
                                    }
                                    is Constructor -> {
                                        if (currentContext.parentClass.constructor.isDefaultConstructor().not())
                                            throw MoreThanOneConstructorDeclarationException(
                                                "la clase actual ya tiene un constructor.",
                                                currentContext.token
                                            )

                                        currentContext.parentClass.constructor = currentContext
                                        currentContext.paramMap = symbolTable.accumulator.params
                                        currentContext.declarationCompleted = true

                                        symbolTable.accumulator.clear()

                                        astBuilder.currentContext = Block(
                                            currentContext,
                                            prevToken,
                                            astBuilder.currentContext as? Sentence
                                        ).also {
                                            currentContext.block = it
                                        }
                                    }
                                    is Method -> {
                                        val throwIMDE = symbolTable.accumulator.modifier.type == TokenType.ABSTRACT

                                        addMethod()

                                        if (throwIMDE)
                                            throw InvalidMethodDeclarationException(
                                                "los métodos abstractos no pueden tener cuerpo.",
                                                currentContext.token
                                            )

                                        astBuilder.currentContext = Block(
                                            currentContext,
                                            prevToken,
                                            astBuilder.currentContext as? Sentence
                                        ).also {
                                            currentContext.block = currentContext.block ?: it
                                        }
                                    }
                                }
                            } else {
                                symbolTable.accumulator.expectedClosingBrackets++

                                when (currentContext) {
                                    is Callable -> {
                                        astBuilder.currentContext = Block(
                                            currentContext,
                                            prevToken,
                                            astBuilder.currentContext as? Sentence
                                        ).also {
                                            when (val currentBlockParent = it.parentSentence) {
                                                is Block -> {
                                                    currentBlockParent.childrenList.add(it)
                                                }
                                                is CompoundSentence -> {
                                                    currentBlockParent.body = it
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        TokenType.MET_VAR_IDENTIFIER -> {
                            when (val currentContext = symbolTable.currentContext) {
                                is FormalArgument -> {
                                    currentContext.token = prevToken
                                    currentContext.typeToken = symbolTable.accumulator.memberType
                                }
                                is Method -> {
                                    if (currentContext.declarationCompleted.not())
                                        currentContext.token = prevToken
                                    else {
                                        when (val astContext = astBuilder.currentContext) {
                                            is LocalVar -> {
                                                astContext.varName = prevToken
                                            }
                                        }
                                    }
                                }
                                is Class -> {
                                    if (currentToken.type == TokenType.LEFT_BRACKET) {
                                        symbolTable.currentContext = Method(
                                            prevToken,
                                            symbolTable.currentClass
                                        ).also { it.typeToken = symbolTable.accumulator.memberType }
                                    } else if (currentToken.type == TokenType.SEMICOLON) {
                                        symbolTable.currentClass.attributeMap.putIfAbsentOrError(
                                            prevToken.lexeme,
                                            Attribute(
                                                prevToken,
                                                symbolTable.currentClass
                                            ).also {
                                                it.typeToken = symbolTable.accumulator.memberType
                                            }
                                        ) {
                                            throw RepeatedDeclarationException(
                                                "un atributo con el mismo nombre ya fue declarado anteriormente en la clase actual.",
                                                prevToken
                                            )
                                        }
                                    }
                                }
                            }

                        }

                        TokenType.COMMA -> {
                            processSTContextForSemicolonOrRightBracket()
                        }

                        TokenType.RIGHT_BRACKET -> {
                            processSTContextForSemicolonOrRightBracket()

                            when (val astContext = astBuilder.currentContext) {
                                is ParenthesizedExpression -> {
                                    astBuilder.currentContext = astContext.parentExpression
                                }
                            }
                        }

                        TokenType.RIGHT_CURLY_BRACKET -> {
                            if (symbolTable.accumulator.expectedClosingBrackets == 0){
                                when (symbolTable.currentContext) {
                                    is Class -> {
                                        symbolTable.currentClass = DummyClass
                                        symbolTable.currentContext = DummyContext
                                        symbolTable.accumulator.clear()
                                    }

                                    else -> {
                                        symbolTable.currentContext = symbolTable.currentClass
                                    }
                                }
                            } else {
                                symbolTable.accumulator.expectedClosingBrackets--

                                when (val prevASTContext = astBuilder.currentContext) {
                                    is Sentence -> {
                                        astBuilder.currentContext = prevASTContext.parentSentence
                                    }
                                }

                                when (val astContext = astBuilder.currentContext) {
                                    is CompoundSentence -> {
                                        astBuilder.currentContext = astContext.parentSentence
                                    }
                                }

                                var astContext = astBuilder.currentContext

                                while (astContext is CompoundSentence && astContext.body != null) {
                                    astContext = (astBuilder.currentContext as CompoundSentence).parentSentence
                                    astBuilder.currentContext = astContext
                                }
                            }
                        }

                        TokenType.SEMICOLON -> {
                            val stContext = symbolTable.currentContext
                            if (stContext is Callable){
                                if (stContext.declarationCompleted.not()) {
                                    if (symbolTable.accumulator.modifier.type == TokenType.ABSTRACT) {
                                        addMethod()
                                    } else
                                        throw InvalidMethodDeclarationException(
                                            "declaración de método concreto sin cuerpo.",
                                            stContext.token
                                        )

                                } else {
                                    when (val prevASTContext = astBuilder.currentContext) {
                                        is CompoundSentence, is LocalVar, is Return -> {
                                            astBuilder.currentContext = prevASTContext.parentSentence
                                        }
                                    }

                                    var astContext = astBuilder.currentContext

                                    while (astContext is CompoundSentence && astContext.body != null) {
                                        astContext = (astBuilder.currentContext as CompoundSentence).parentSentence
                                        astBuilder.currentContext = astContext
                                    }
                                }
                            }
                        }

                        TokenType.IF -> {
                            astBuilder.currentContext = If(
                                symbolTable.currentContext as Callable,
                                prevToken,
                                astBuilder.currentContext as Sentence
                            ).also {
                                when (val parent = it.parentSentence) {
                                    is Block -> {
                                        parent.childrenList.add(it)
                                    }
                                    is CompoundSentence -> {
                                        parent.body = it
                                    }
                                }
                            }
                        }

                        TokenType.ELSE -> {
                            astBuilder.currentContext = Else(
                                symbolTable.currentContext as Callable,
                                prevToken,
                                astBuilder.currentContext as Sentence
                            ).also {
                                when (val parent = it.parentSentence) {
                                    is Block -> {
                                        parent.childrenList.add(it)
                                    }
                                    is If -> {
                                        parent.elseSentence = it
                                    }
                                }
                            }
                        }

                        TokenType.WHILE -> {
                            astBuilder.currentContext = While(
                                symbolTable.currentContext as Callable,
                                prevToken,
                                astBuilder.currentContext as Sentence
                            ).also {
                                when (val parent = it.parentSentence) {
                                    is Block -> {
                                        parent.childrenList.add(it)
                                    }
                                    is CompoundSentence -> {
                                        parent.body = it
                                    }
                                }
                            }
                        }

                        TokenType.RETURN -> {
                            astBuilder.currentContext = Return(
                                symbolTable.currentContext as Callable,
                                prevToken,
                                astBuilder.currentContext as Sentence
                            ).also {
                                when (val parent = it.parentSentence) {
                                    is Block -> {
                                        parent.childrenList.add(it)
                                    }
                                    is CompoundSentence -> {
                                        parent.body = it
                                    }
                                }
                            }
                        }

                        else -> {}
                    }
                }
            }
            currentStackElement = expectedElementsStack.removeFirst()
        }


    }

    private fun addMethod() {
        val currentContext = symbolTable.currentContext as Method

        if (currentContext.typeToken.isDummyToken())
            currentContext.typeToken = symbolTable.accumulator.memberType

        currentContext.paramMap = symbolTable.accumulator.params
        currentContext.modifier = symbolTable.accumulator.modifier
        currentContext.declarationCompleted = true

        if (currentContext.modifier.type == TokenType.ABSTRACT &&
            symbolTable.currentClass.modifier.type != TokenType.ABSTRACT)
            throw InvalidMethodDeclarationException(
                "declaración de método abstracto en clase concreta.",
                currentContext.token
            )

        symbolTable.currentClass.methodMap.putIfAbsentOrError(
            currentContext.token.lexeme,
            currentContext
        ) {
            throw RepeatedDeclarationException(
                "un método con el mismo nombre fue declarado anteriormente en la clase actual.",
                currentContext.token
            )
        }

        symbolTable.accumulator.clear()
    }

    /**
     * When currentToken's type equals expected TokenType asks lexer for next token.
     * Otherwise, throws MismatchException
     */
    private fun match(expectedElement: TokenType) {
        if ((expectedElement == currentToken.type).not())
            throw MismatchException(
                token = currentToken,
                expected = setOf(expectedElement)
            )
        currentToken = lexer.getNextToken()
    }

    private fun matchAndReturn(expectedElement: TokenType): Token {
        if ((expectedElement == currentToken.type).not())
            throw MismatchException(
                token = currentToken,
                expected = setOf(expectedElement)
            )

        return currentToken.also { currentToken = lexer.getNextToken() }
    }

    /**
     * Matches currentToken if it's in nonTerminal's firsts.
     * Should be used when void production is not an option.
     * @throws MismatchException when currentToken is not in nonTerminal's firsts
     */
    private fun matchAnyInFirst(nonTerminal: NonTerminal) {
        if (currentToken.type in nonTerminal.first)
            match(currentToken.type)
        else
            throw MismatchException(
                token = currentToken,
                nonTerminal.first
            )
    }

    /**
     * Matches currentToken if it's in nonTerminal's firsts and returns true, returns false otherwise.
     * Should be used when void production is an option.
     */
    private fun tryMatchAnyInFirst(nonTerminal: NonTerminal): Boolean {
        val couldMatch = currentToken.type in nonTerminal.first

        if (couldMatch)
            match(currentToken.type)

        return couldMatch
    }

    private fun matchAnyInNext(nonTerminal: NonTerminal) {
        if (currentToken.type in follow[nonTerminal])
            match(currentToken.type)
        else
            throw MismatchException(
                token = currentToken,
                nonTerminal.first
            )
    }

    /**
     * Matches currentToken if it's in nonTerminal's firsts and returns true, returns false otherwise.
     * Should be used when void production is an option.
     */
    private fun tryMatchAnyInNext(nonTerminal: NonTerminal): Boolean {
        val couldMatch = currentToken.type in follow[nonTerminal]

        if (couldMatch)
            match(currentToken.type)

        return couldMatch
    }

    private fun Token.inFirsts(nonTerminal: NonTerminal) =
        nonTerminal.first.contains(this.type)

    private fun Token.inNexts(nonTerminal: NonTerminal) =
        follow[nonTerminal].contains(this.type)

    private operator fun Array<Set<TokenType>>.get(nonTerminal: NonTerminal) =
        follow[nonTerminal.ordinal]

    private fun throwUnexpectedTerminalException(currentNT: NonTerminal) {
        throw UnexpectedTerminalException(currentToken, setOf(currentNT) + follow[currentNT])
    }

    private inline fun <DeclarableInstance: Declarable> MutableMap<String, DeclarableInstance>.putIfAbsentOrError(
        key: String,
        value: DeclarableInstance,
        errorFunction: () -> Unit
    ) {
        if (this.contains(key)) {
            errorFunction()
        }

        this[key] = value
        value.declarationCompleted = true
    }

    private inline fun MutableMap<String, MutableSet<Attribute>>.putIfAbsentOrError(
        key: String,
        value: Attribute,
        errorFunction: () -> Unit
    ) {
        if (this.contains(key)) {
            errorFunction()
        }

        this[key] = mutableSetOf(value)
        value.declarationCompleted = true
    }

    private fun processSTContextForSemicolonOrRightBracket() {
        when (val currentContext = symbolTable.currentContext) {
            is FormalArgument -> {
                symbolTable.accumulator.params.putIfAbsentOrError(
                    currentContext.token.lexeme,
                    currentContext
                ) {
                    throw RepeatedDeclarationException(
                        "ya hay un parámetro declarado con ese nombre en el contexto actual",
                        currentContext.token
                    )
                }
                symbolTable.currentContext = currentContext.member
            }
        }
    }
}