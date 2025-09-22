package utils

import utils.TokenType.*

enum class NonTerminal(val first: Set<TokenType>): SyntacticStackable {
    OPTIONAL_INHERITANCE(
        first = setOf(EXTENDS)
    ),
    MODIFIER(
        first = setOf(ABSTRACT, FINAL, STATIC)
    ),
    OPTIONAL_MODIFIER(
        first = MODIFIER.first
    ),
    CLASS(
        first = OPTIONAL_MODIFIER.first + TokenType.CLASS
    ),
    CLASS_LIST(
        first = CLASS.first
    ),
    INITIAL(
        first = CLASS.first
    ),
    TYPE(
        first = setOf(BOOLEAN, CHAR, INT, CLASS_IDENTIFIER)
    ),
    CONSTRUCTOR(
        first = setOf(PUBLIC)
    ),
    MEMBER(
        first = TYPE.first + VOID + MODIFIER.first + CONSTRUCTOR.first
    ),
    MEMBER_LIST(
        first = MEMBER.first
    ),
    METHOD_TYPE(
        first = TYPE.first + VOID
    ),
    REST_OF_MEMBER_DECLARATION(
        first = setOf(MET_VAR_IDENTIFIER)
    ),
    FORMAL_ARGUMENTS(
        first = setOf(LEFT_BRACKET)
    ),
    BLOCK(
        first = setOf(LEFT_CURLY_BRACKET)
    ),
    END_OF_MEMBER_DECLARATION(
        first = setOf(SEMICOLON) + FORMAL_ARGUMENTS.first
    ),
    FORMAL_ARGUMENT(
        first = TYPE.first
    ),
    FORMAL_ARGUMENTS_LIST(
        first = FORMAL_ARGUMENT.first
    ),
    OPTIONAL_FORMAL_ARGUMENTS_LIST(
        first = FORMAL_ARGUMENTS_LIST.first
    ),
    REST_OF_FORMAL_ARGUMENTS_LIST(
        first = setOf(COMMA)
    ),
    OPTIONAL_BLOCK(
        first = BLOCK.first + setOf(SEMICOLON)
    ),
    UNARY_OPERATOR(
        first = setOf(ADDITION, INCREMENT, SUBSTRACTION, DECREMENT, NOT)
    ),
    PRIMITIVE(
        first = setOf(TRUE, FALSE, INTEGER_LITERAL, CHAR_LITERAL, NULL)
    ),
    CONSTRUCTOR_CALL(
        first = setOf(NEW)
    ),
    STATIC_METHOD_CALL(
        first = setOf(CLASS_IDENTIFIER)
    ),
    PARENTHESIZED_EXPRESSION(
        first = setOf(LEFT_BRACKET)
    ),
    VAR_ACCESS(
        first = setOf(MET_VAR_IDENTIFIER)
    ),
    PRIMARY(
        first = setOf(
            THIS, STRING_LITERAL, MET_VAR_IDENTIFIER) +
            VAR_ACCESS.first + CONSTRUCTOR_CALL.first +
            STATIC_METHOD_CALL.first + PARENTHESIZED_EXPRESSION.first

    ),
    ACTUAL_ARGUMENTS(
        first = setOf(LEFT_BRACKET)
    ),
    REST_OF_METHOD_CALL_OR_VAR_ACCESS(
        first = ACTUAL_ARGUMENTS.first
    ),
    REFERENCE(
        first = PRIMARY.first
    ),
    OPERAND(
        first = PRIMITIVE.first + REFERENCE.first
    ),
    BASIC_EXPRESSION(
        first = UNARY_OPERATOR.first + OPERAND.first
    ),
    BINARY_OPERATOR(
        first = setOf(
            OR, AND, EQUALS, DIFFERENT, LESS_THAN, GREATER_THAN,
            LESS_THAN_OR_EQUAL, GREATER_THAN_OR_EQUAL, ADDITION,
            SUBSTRACTION, MULTIPLICATION, DIVISION, MODULUS
        )
    ),
    COMPOUND_EXPRESSION(
        first = BASIC_EXPRESSION.first
    ),
    REST_OF_COMPOUND_EXPRESSION(
        first = BINARY_OPERATOR.first
    ),
    EXPRESSION(
        first = COMPOUND_EXPRESSION.first
    ),
    ASSIGNMENT_OPERATOR(
        first = setOf(ASSIGNMENT)
    ),
    REST_OF_EXPRESSION(
        first = ASSIGNMENT_OPERATOR.first
    ),
    LOCAL_VARIABLE(
        first = setOf(VAR)
    ),
    RETURN(
        first = setOf(TokenType.RETURN)
    ),
    OPTIONAL_EXPRESSION(
        first = EXPRESSION.first
    ),
    IF(
        first = setOf(TokenType.IF)
    ),
    OPTIONAL_ELSE(
        first = setOf(ELSE)
    ),
    WHILE(
        first = setOf(TokenType.WHILE)
    ),
    SENTENCE(
        first =
            setOf(SEMICOLON) +
            EXPRESSION.first +
            LOCAL_VARIABLE.first +
            RETURN.first +
            IF.first +
            WHILE.first +
            BLOCK.first
    ),
    SENTENCE_LIST(
        first = SENTENCE.first
    ),
    EXPRESSION_LIST(
        first = EXPRESSION.first
    )
    ,
    OPTIONAL_EXPRESSION_LIST(
        first = EXPRESSION_LIST.first
    ),
    REST_OF_EXPRESSION_LIST(
        first = setOf(COMMA)
    ),
    MET_VAR_ACCESS(
        first = setOf(DOT)
    ),
    REST_OF_MET_VAR_ACCESS(
        first = ACTUAL_ARGUMENTS.first
    ),
    REST_OF_REFERENCE(
        first = MET_VAR_ACCESS.first
    );

    companion object {
        val follow = Array<Set<TokenType>>(entries.size) {
            emptySet()
        }

        init {
            follow[CLASS_LIST.ordinal] = setOf(EOF)
            follow[CLASS.ordinal] = CLASS_LIST.first + follow[CLASS_LIST.ordinal]
            follow[OPTIONAL_MODIFIER.ordinal] = setOf(TokenType.CLASS)
            follow[OPTIONAL_INHERITANCE.ordinal] = setOf(LEFT_CURLY_BRACKET)
            follow[MEMBER_LIST.ordinal] = setOf(RIGHT_CURLY_BRACKET)
            follow[MEMBER.ordinal] = setOf(
                PUBLIC, ABSTRACT, STATIC, FINAL, BOOLEAN, CHAR, INT, CLASS_IDENTIFIER, VOID, RIGHT_CURLY_BRACKET
            )
            follow[TYPE.ordinal] = setOf(MET_VAR_IDENTIFIER)
            follow[REST_OF_MEMBER_DECLARATION.ordinal] = setOf(SEMICOLON, LEFT_BRACKET)
            follow[MODIFIER.ordinal] = setOf(BOOLEAN, CHAR, INT, CLASS_IDENTIFIER, VOID)
            follow[METHOD_TYPE.ordinal] = setOf(MET_VAR_IDENTIFIER)
            follow[CONSTRUCTOR.ordinal] = setOf(
                PUBLIC, ABSTRACT, STATIC, FINAL, BOOLEAN, CHAR, INT, CLASS_IDENTIFIER, VOID, RIGHT_CURLY_BRACKET
            )
            follow[FORMAL_ARGUMENTS.ordinal] = setOf(LEFT_CURLY_BRACKET, SEMICOLON)
            follow[OPTIONAL_BLOCK.ordinal] = setOf(
                PUBLIC, ABSTRACT, STATIC, FINAL, BOOLEAN, CHAR, INT, CLASS_IDENTIFIER, VOID, RIGHT_CURLY_BRACKET
            )
            follow[BLOCK.ordinal]
        }

    }

}
