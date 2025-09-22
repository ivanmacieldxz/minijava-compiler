package parser

import lexer.LexicalAnalyzer
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
                                //TODO: comportamiento para cuando el token actual no está en los siguientes
//                                throw UnexpectedTerminalException(currentToken, follow[currentStackElement])
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
                        }

                        NonTerminal.OPTIONAL_MODIFIER -> {
                            if (currentToken.inFirsts(currentStackElement)) {
                                expectedElementsStack.addFirst(NonTerminal.MODIFIER)
                            } else if (currentToken.inNexts(currentStackElement).not()) {
                                //TODO: comportamiento para cuando el token actual no está en los siguientes
//                                throw UnexpectedTerminalException(currentToken, follow[currentStackElement])
                            }
                        }

                        NonTerminal.MODIFIER -> {
                            matchAny(currentStackElement)
                        }

                        NonTerminal.OPTIONAL_INHERITANCE -> {
                            if (tryMatchAny(currentStackElement)) {
                                match(TokenType.CLASS_IDENTIFIER)
                            }
                        }

                        NonTerminal.MEMBER_LIST -> {
                            if (currentToken.inFirsts(currentStackElement)) {
                                expectedElementsStack.addFirst(NonTerminal.MEMBER_LIST)
                                expectedElementsStack.addFirst(NonTerminal.MEMBER)
                            } else if (currentToken.inNexts(currentStackElement).not()) {
                                //TODO: comportamiento para cuando el token actual no está en los siguientes
//                                throw UnexpectedTerminalException(currentToken, follow[currentStackElement])
                            }
                        }

                        NonTerminal.MEMBER -> {
                            if (currentToken.inFirsts(NonTerminal.TYPE)) {
                                expectedElementsStack.addFirst(NonTerminal.REST_OF_MEMBER_DECLARATION)
                                expectedElementsStack.addFirst(NonTerminal.TYPE)
                            } else if (currentToken.inFirsts(NonTerminal.CONSTRUCTOR)) {
                                expectedElementsStack.addFirst(NonTerminal.CONSTRUCTOR)
                            } else if (currentToken.inFirsts(NonTerminal.MODIFIER)) {
                                expectedElementsStack.addFirst(NonTerminal.REST_OF_MEMBER_DECLARATION)
                                expectedElementsStack.addFirst(NonTerminal.METHOD_TYPE)
                                expectedElementsStack.addFirst(NonTerminal.MODIFIER)
                            } else {
                                expectedElementsStack.addFirst(NonTerminal.REST_OF_MEMBER_DECLARATION)
                                match(TokenType.VOID)
                            }
                        }

                        NonTerminal.TYPE -> {
                            matchAny(currentStackElement)
                        }

                        NonTerminal.REST_OF_MEMBER_DECLARATION -> {
                            expectedElementsStack.addFirst(NonTerminal.END_OF_MEMBER_DECLARATION)
                            matchAny(currentStackElement)
                        }

                        NonTerminal.END_OF_MEMBER_DECLARATION -> {
                            //TODO: cambiar para que acepte también la otra rama, la de args_formales y bloque_opcional
                            match(TokenType.SEMICOLON)
                        }

                        NonTerminal.METHOD_TYPE -> {
                            matchAny(currentStackElement)
                        }

                        NonTerminal.CONSTRUCTOR -> {
                            matchAny(currentStackElement)
                            match(TokenType.CLASS_IDENTIFIER)
                            expectedElementsStack.addFirst(NonTerminal.BLOCK)
                            expectedElementsStack.addFirst(NonTerminal.FORMAL_ARGUMENTS)
                        }

                        NonTerminal.FORMAL_ARGUMENTS -> {
                            matchAny(currentStackElement)
                            expectedElementsStack.addFirst(NonTerminal.OPTIONAL_FORMAL_ARGUMENTS_LIST)
                        }

                        NonTerminal.OPTIONAL_FORMAL_ARGUMENTS_LIST -> {
                            match(TokenType.RIGHT_BRACKET)
                        }

                        else -> {

                        }
                    }
                }
                is TokenType -> {
                    match(currentStackElement)
                }
            }

            currentStackElement = expectedElementsStack.removeFirst()
        }

        //está para el caso en que llegue eof cuando no se lo esperaba,
        //el resto de not in firsts error deberían lanzarse dentro de cada situación específica
        if (expectedElementsStack.isNotEmpty()) {
            throw UnexpectedTerminalException(currentToken, setOf(currentStackElement as TokenType))
        }
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

    /**
     * Matches currentToken if it's in nonTerminal's firsts.
     * Should be used when void production is not an option.
     * @throws MismatchException when currentToken is not in nonTerminal's firsts
     */
    private fun matchAny(nonTerminal: NonTerminal) {
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
    private fun tryMatchAny(nonTerminal: NonTerminal): Boolean {
        val couldMatch = currentToken.type in nonTerminal.first

        if (couldMatch)
            match(currentToken.type)

        return couldMatch
    }

    private fun Token.inFirsts(nonTerminal: NonTerminal) =
        nonTerminal.first.contains(this.type)

    private fun Token.inNexts(nonTerminal: NonTerminal) =
        NonTerminal.follow[nonTerminal].contains(this.type)

    private operator fun Array<Set<TokenType>>.get(nonTerminal: NonTerminal) =
        follow[nonTerminal.ordinal]
}