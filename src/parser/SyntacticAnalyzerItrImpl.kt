package parser

import lexer.LexicalAnalyzer
import utils.NonTerminal
import utils.SyntacticStackable
import utils.Token
import utils.TokenType
import kotlin.collections.setOf
import kotlin.enums.enumEntries

class SyntacticAnalyzerItrImpl(
    private val lexer: LexicalAnalyzer
) {
    private var expectedElementsStack = ArrayDeque<SyntacticStackable>()
    private lateinit var currentToken: Token
    private val firsts = Array<Set<TokenType>>(enumEntries<NonTerminal>().size){
        setOf()
    }

    init {
        firsts[NonTerminal.INITIAL.ordinal]
        firsts[NonTerminal.CLASS.ordinal]
        firsts[NonTerminal.CLASS_LIST.ordinal]
        firsts[NonTerminal.OPTIONAL_MODIFIER.ordinal] = setOf(
            TokenType.ABSTRACT,
            TokenType.STATIC,
            TokenType.FINAL
        )
        firsts[NonTerminal.OPTIONAL_INHERITANCE.ordinal] = setOf(
            TokenType.EXTENDS
        )
        firsts[NonTerminal.MEMBER_LIST.ordinal]
        firsts[NonTerminal.MEMBER.ordinal]
        firsts[NonTerminal.TYPE.ordinal]
        firsts[NonTerminal.REST_OF_MEMBER_DECLARATION.ordinal]
        firsts[NonTerminal.REST_OF_METHOD_DECLARATION.ordinal]
        firsts[NonTerminal.MODIFIER.ordinal]
        firsts[NonTerminal.METHOD_TYPE.ordinal]
        firsts[NonTerminal.CONSTRUCTOR.ordinal]
    }

    fun start() {
        expectedElementsStack.addFirst(NonTerminal.INITIAL)
        currentToken = lexer.getNextToken()
        var currentQueueElement = expectedElementsStack.removeFirst()

        while (currentQueueElement != TokenType.EOF) {
            when (currentQueueElement) {
                is NonTerminal -> {
                    when (currentQueueElement) {
                        NonTerminal.INITIAL -> {
                            if ((currentToken == Token.EOFToken).not()) {
                                expectedElementsStack.addFirst(TokenType.EOF)
                                expectedElementsStack.addFirst(NonTerminal.CLASS_LIST)
                            } else {
                                break
                            }
                        }

                        NonTerminal.CLASS_LIST -> {
                            expectedElementsStack.addFirst(TokenType.RIGHT_CURLY_BRACKET)
                            expectedElementsStack.addFirst(NonTerminal.MEMBER_LIST)
                            expectedElementsStack.addFirst(TokenType.LEFT_CURLY_BRACKET)
                            expectedElementsStack.addFirst(NonTerminal.OPTIONAL_INHERITANCE)
                            expectedElementsStack.addFirst(TokenType.CLASS_IDENTIFIER)
                            expectedElementsStack.addFirst(TokenType.CLASS)
                            expectedElementsStack.addFirst(NonTerminal.OPTIONAL_MODIFIER)
                        }

                        NonTerminal.CLASS -> {

                        }

                        NonTerminal.OPTIONAL_MODIFIER -> {
                            matchIfInFirsts(currentQueueElement)
                            //no hay terminales obligatorios después del modificador como tal, por eso difiere del de abajo

                            //matcheo los que me quedaban pendientes del anterior
                            match(TokenType.CLASS)
                            match(TokenType.CLASS_IDENTIFIER)
                        }

                        NonTerminal.OPTIONAL_INHERITANCE -> {
                            if (matchIfInFirsts(currentQueueElement)) {
                                //matcheo el resto de los terminales obligatorios
                                expectedElementsStack.addFirst(TokenType.CLASS_IDENTIFIER)
                                match(TokenType.CLASS_IDENTIFIER)
                            }
                            match(TokenType.LEFT_CURLY_BRACKET)
                        }

                        NonTerminal.MEMBER_LIST -> {
                            //TODO: lista de miembros real
                            match(TokenType.RIGHT_CURLY_BRACKET)
                        }

                        NonTerminal.MEMBER -> {

                        }

                        NonTerminal.TYPE -> {

                        }

                        NonTerminal.REST_OF_MEMBER_DECLARATION -> {

                        }

                        NonTerminal.REST_OF_METHOD_DECLARATION -> {

                        }

                        NonTerminal.MODIFIER -> {

                        }

                        NonTerminal.METHOD_TYPE -> {

                        }

                        NonTerminal.CONSTRUCTOR -> {

                        }
                    }
                }
                else -> {
                    match(currentQueueElement as TokenType)
                }
            }

            currentQueueElement = expectedElementsStack.removeFirst()
        }

        //está para el caso en que llegue eof cuando no se lo esperaba,
        //el resto de not in firsts error deberían lanzarse dentro de cada situación específica
        if (expectedElementsStack.isNotEmpty()) {
            throw NotInFirstsException(currentToken, currentQueueElement as TokenType)
        }
    }

    /**
     * When currentToken's type equals expected TokenType, pops from expectedElementsStack
     * and asks lexer for next token. Otherwise, throws MismatchException
     */
    private fun match(expectedElement: TokenType) {
        if ((expectedElement == currentToken.type).not())
            throw MismatchException(
                token = currentToken,
                expected = expectedElement
            )
        expectedElementsStack.removeFirst()
        currentToken = lexer.getNextToken()
    }

    /**
     * Matches currentToken with received nonterminal if it's on its firsts.
     * Matching implies --> going to next token, or throwing MismatchException.
     * Should be used when trying to match optional terminals (TODO: maybe under that condition?)
     */
    private fun matchIfInFirsts(currentQueueElement: NonTerminal): Boolean {
        var matched = firsts[currentQueueElement.ordinal].contains(currentToken.type)

        if (matched) {
            currentToken = lexer.getNextToken()
            matched = true
        }

        return matched
    }
}