package utils

import utils.TokenType.*

enum class NonTerminal(val first: Set<TokenType>): SyntacticStackable {

    MODIFIER(
        setOf(ABSTRACT, STATIC, FINAL)
    ),

    // <ModificadorOpcional> --> abstract | static | final | e
    OPTIONAL_MODIFIER(
        MODIFIER.first
    ),

    // <Clase> --> <ModificadorOpcional> class idClase <HerenciaOpcional> { <ListaMiembros> }
    CLASS(
        OPTIONAL_MODIFIER.first + TokenType.CLASS
    ),

    // <ListaClases> --> <Clase> <ListaClases> | e
    CLASS_LIST(
        CLASS.first
    ),

    // <Inicial> --> <ListaClases> eof
    INITIAL(
        CLASS_LIST.first
    ),

    // <HerenciaOpcional> --> extends idClase | e
    OPTIONAL_INHERITANCE(
        setOf(EXTENDS)
    ),

    // <TipoPrimitivo> --> boolean | char | int
    PRIMITIVE_TYPE(
        setOf(BOOLEAN, CHAR, INT)
    ),

    // <Tipo> --> <TipoPrimitivo> | idClase
    TYPE(
        PRIMITIVE_TYPE.first + CLASS_IDENTIFIER
    ),

    // <Miembro> --> <Tipo> <RestoDeclaracionMiembro> |
    //    void <RestoDeclaracionMetodo> |
    //    <Modificador> <TipoMetodo> <RestoDeclaracionMetodo> |
    //    <Constructor>
    MEMBER(
        TYPE.first + VOID + OPTIONAL_MODIFIER.first + PUBLIC
    ),

    // <ListaMiembros> --> <Miembro> <ListaMiembros> | e
    MEMBER_LIST(
        MEMBER.first
    ),

    // <RestoDeclaracionMiembro> --> idMetVar <FinDeclaracionMiembro>
    REST_OF_MEMBER_DECLARATION(
        setOf(MET_VAR_IDENTIFIER)
    ),

    // <ArgsFormales> --> ( <ListaArgsFormalesOpcional> )
    FORMAL_ARGUMENTS(
        setOf(LEFT_BRACKET)
    ),

    // <RestoDeclaracionMetodo> --> <ArgsFormales> <BloqueOpcional>
    REST_OF_METHOD_DECLARATION(
        FORMAL_ARGUMENTS.first
    ),

    // <FinDeclaracionMiembro> --> ; | <RestoDeclaracionMetodo>
    END_OF_MEMBER_DECLARATION(
        setOf(SEMICOLON) + REST_OF_METHOD_DECLARATION.first
    ),

    // <Constructor> --> public idClase <ArgsFormales> <Bloque>
    CONSTRUCTOR(
        setOf(PUBLIC)
    ),

    // <TipoMetodo> --> <Tipo> | void
    METHOD_TYPE(
        TYPE.first + VOID
    ),

    // <ArgFormal> --> <Tipo> idMetVar
    FORMAL_ARGUMENT(
        TYPE.first
    ),

    // <ListaArgsFormales> --> <ArgFormal> <RestoListaArgsFormales>
    FORMAL_ARGUMENTS_LIST(
        FORMAL_ARGUMENT.first
    ),

    // <ListaArgsFormalesOpcional> --> <ListaArgsFormales> | e
    OPTIONAL_FORMAL_ARGUMENTS_LIST(
        FORMAL_ARGUMENTS_LIST.first
    ),

    // <RestoListaArgsFormales> --> , <ListaArgsFormales> | e
    REST_OF_FORMAL_ARGUMENTS_LIST(
        setOf(COMMA)
    ),

    // <Bloque> --> { <ListaSentencias> }
    BLOCK(
        setOf(LEFT_CURLY_BRACKET)
    ),

    // <BloqueOpcional> --> <Bloque> | ;
    OPTIONAL_BLOCK(
        BLOCK.first + SEMICOLON
    ),

    // <VarLocal> --> var idMetVar = <ExpresionCompuesta>
    LOCAL_VARIABLE(
        setOf(VAR)
    ),

    // <Return> --> return <ExpresionOpcional>
    RETURN(
        setOf(TokenType.RETURN)
    ),

    // <OperadorUnario> --> + | ++ | - | -- | !
    UNARY_OPERATOR(
        setOf(ADDITION, INCREMENT, SUBSTRACTION, DECREMENT, NOT)
    ),

    // <Primitivo> --> true | false | intLiteral | charLiteral | null
    PRIMITIVE(
        setOf(TRUE, FALSE, INTEGER_LITERAL, CHAR_LITERAL, NULL)
    ),

    // <AccesoVarLlamadaMet> --> idMetVar <RestoLlamadaMetOpcional>
    VAR_ACCESS_OR_MET_CALL(
        setOf(MET_VAR_IDENTIFIER)
    ),

    // <LlamadaConstructor> --> new idClase <ArgsActuales>
    CONSTRUCTOR_CALL(
        setOf(NEW)
    ),

    // <ExpresionParentizada> --> ( <Expresion> )
    PARENTHESIZED_EXPRESSION(
        setOf(LEFT_BRACKET)
    ),

    // <LlamadaMetodoEstatico> --> idClase . idMetVar <ArgsActuales>
    STATIC_METHOD_CALL(
        setOf(CLASS_IDENTIFIER)
    ),

    // <Primario> --> this | stringLiteral | <AccesoVarLlamadaMet> | <LlamadaConstructor> | <LlamadaMetodoEstatico> | <ExpresionParentizada>
    PRIMARY(
        setOf(THIS, STRING_LITERAL) + VAR_ACCESS_OR_MET_CALL.first + CONSTRUCTOR_CALL.first + STATIC_METHOD_CALL.first + PARENTHESIZED_EXPRESSION.first
    ),

    // <Referencia> --> <Primario> <RestoReferencia>
    REFERENCE(
        PRIMARY.first
    ),

    // <Operando> --> <Primitivo> | <Referencia>
    OPERAND(
        PRIMITIVE.first + REFERENCE.first
    ),

    // <ExpresionBasica> --> <OperadorUnario> <Operando> | <Operando>
    BASIC_EXPRESSION(
        UNARY_OPERATOR.first + OPERAND.first
    ),

    // <ExpresionCompuesta> --> <ExpresionBasica> <RestoExpresionCompuesta>
    COMPOUND_EXPRESSION(
        BASIC_EXPRESSION.first
    ),

    // <Expresion> --> <ExpresionCompuesta> <RestoExpresion>
    EXPRESSION(
        COMPOUND_EXPRESSION.first
    ),

    // <If> --> if ( <Expresion> ) <Sentencia> <ElseOpcional>
    IF(
        setOf(TokenType.IF)
    ),

    OPTIONAL_ELSE (
        setOf(ELSE)
    ),

    // <While> --> while ( <Expresion> ) <Sentencia>
    WHILE(
        setOf(TokenType.WHILE)
    ),

    // <Sentencia> --> ; | <Expresion> ; | <VarLocal> ; | <Return> ; | <If> | <While> | <Bloque>
    SENTENCE(
        setOf(SEMICOLON) + EXPRESSION.first + LOCAL_VARIABLE.first + RETURN.first + IF.first + WHILE.first + BLOCK.first
    ),

    // <ListaSentencias> --> <Sentencia> <ListaSentencias> | e
    SENTENCE_LIST(
        SENTENCE.first
    ),

    // <ExpresionOpcional> --> <Expresion> | e
    OPTIONAL_EXPRESSION(
        EXPRESSION.first
    ),

    // <OperadorAsignacion> --> =
    ASSIGNMENT_OPERATOR(
        setOf(ASSIGNMENT)
    ),

    // <RestoExpresion> --> <OperadorAsignacion> <ExpresionCompuesta> | e
    REST_OF_EXPRESSION(
        ASSIGNMENT_OPERATOR.first
    ),

    // <OperadorBinario> --> || | && | == | != | < | > | <= | >= | + | - | * | / | %
    BINARY_OPERATOR(
        setOf(OR, AND, EQUALS, DIFFERENT, LESS_THAN, GREATER_THAN, LESS_THAN_OR_EQUAL,
            GREATER_THAN_OR_EQUAL, ADDITION, SUBSTRACTION, MULTIPLICATION, DIVISION, MODULUS)
    ),

    // <RestoExpresionCompuesta> --> <OperadorBinario> <ExpresionBasica> <RestoExpresionCompuesta> | e
    REST_OF_COMPOUND_EXPRESSION(
        BINARY_OPERATOR.first
    ),

    // <MetVarEncadenada> --> .idMetVar <RestoDeEncadenamiento>
    CHAINED_MET_VAR(
        setOf(DOT)
    ),

    // <RestoReferencia> --> <MetVarEncadenada> <RestoReferencia> | e
    REST_OF_REFERENCE(
        CHAINED_MET_VAR.first
    ),

    // <ArgsActuales> --> ( <ListaExpsOpcional> )
    ACTUAL_ARGUMENTS(
        setOf(LEFT_BRACKET)
    ),

    // <RestoLlamadaMetOpcional> --> <ArgsActuales> | e
    REST_OF_OPTIONAL_METHOD_CALL(
        ACTUAL_ARGUMENTS.first
    ),

    // <ListaExps> --> <Expresion> <RestoListaExps>
    EXPRESSION_LIST(
        EXPRESSION.first
    ),

    // <ListaExpsOpcional> --> <ListaExps> | e
    OPTIONAL_EXPRESSION_LIST(
        EXPRESSION_LIST.first
    ),

    // <RestoListaExps> --> , <ListaExps> | e
    REST_OF_EXPRESSION_LIST(
        setOf(COMMA)
    ),

    // <RestoDeEncadenamiento> --> <ArgsActuales> | e
    REST_OF_CHAINING(
        ACTUAL_ARGUMENTS.first
    );

    companion object {
        val follow = Array<Set<TokenType>>(entries.size) {
            emptySet()
        }

        init {

            follow[CLASS_LIST] = setOf(EOF)
            follow[CLASS] = CLASS_LIST.first + follow[CLASS_LIST]

            follow[OPTIONAL_MODIFIER] = setOf(TokenType.CLASS)
            follow[OPTIONAL_INHERITANCE] = setOf(LEFT_CURLY_BRACKET)
            follow[MEMBER_LIST] = setOf(RIGHT_CURLY_BRACKET)

            follow[OPTIONAL_FORMAL_ARGUMENTS_LIST] = setOf(RIGHT_BRACKET)
            follow[FORMAL_ARGUMENTS_LIST] = follow[OPTIONAL_FORMAL_ARGUMENTS_LIST]
            follow[REST_OF_FORMAL_ARGUMENTS_LIST] = follow[FORMAL_ARGUMENTS_LIST]

            // <ListaArgsFormales> --> <ArgFormal> <RestoListaArgsFormales>
            follow[FORMAL_ARGUMENT] = REST_OF_FORMAL_ARGUMENTS_LIST.first + follow[REST_OF_FORMAL_ARGUMENTS_LIST]

            // <ArgFormal> --> <Tipo> idMetVar
            // <Miembro> --> <Tipo> <RestoDeclaracionMiembro>
            follow[TYPE] = setOf(MET_VAR_IDENTIFIER) + REST_OF_MEMBER_DECLARATION.first
            follow[PRIMITIVE_TYPE] = follow[TYPE]

            // <Bloque> --> { <ListaSentencias> }
            follow[SENTENCE_LIST] = setOf(RIGHT_CURLY_BRACKET)
            follow[SENTENCE] = SENTENCE_LIST.first + follow[SENTENCE_LIST]
            follow[BLOCK] = follow[SENTENCE] + follow[OPTIONAL_BLOCK]

            // <Sentencia> --> ...
            follow[IF] = follow[SENTENCE]
            follow[OPTIONAL_ELSE] = follow[IF]
            follow[RETURN] = setOf(SEMICOLON)
            follow[LOCAL_VARIABLE] = setOf(SEMICOLON)
            follow[OPTIONAL_EXPRESSION] = follow[RETURN]

            // <Expresion> y relacionados
            follow[EXPRESSION_LIST] = setOf(RIGHT_BRACKET) // De <ListaExpsOpcional>
            follow[REST_OF_EXPRESSION_LIST] = follow[EXPRESSION_LIST]
            follow[EXPRESSION] = setOf(SEMICOLON, RIGHT_BRACKET) + REST_OF_EXPRESSION_LIST.first + follow[REST_OF_EXPRESSION_LIST] + follow[OPTIONAL_EXPRESSION]
            follow[REST_OF_EXPRESSION] = follow[EXPRESSION]
            follow[ASSIGNMENT_OPERATOR] = COMPOUND_EXPRESSION.first
            follow[COMPOUND_EXPRESSION] = REST_OF_EXPRESSION.first + follow[REST_OF_EXPRESSION] + follow[LOCAL_VARIABLE]
            follow[REST_OF_COMPOUND_EXPRESSION] = follow[COMPOUND_EXPRESSION]
            follow[BINARY_OPERATOR] = BASIC_EXPRESSION.first
            follow[BASIC_EXPRESSION] = REST_OF_COMPOUND_EXPRESSION.first + follow[REST_OF_COMPOUND_EXPRESSION]
            follow[UNARY_OPERATOR] = OPERAND.first
            follow[OPERAND] = follow[BASIC_EXPRESSION]
            follow[PRIMITIVE] = follow[OPERAND]

            // <Referencia> y relacionados
            follow[REFERENCE] = follow[OPERAND]
            follow[REST_OF_REFERENCE] = follow[REFERENCE]
            follow[PRIMARY] = REST_OF_REFERENCE.first + follow[REST_OF_REFERENCE]
            follow[VAR_ACCESS_OR_MET_CALL] = follow[PRIMARY]
            follow[CONSTRUCTOR_CALL] = follow[PRIMARY]
            follow[STATIC_METHOD_CALL] = follow[PRIMARY]
            follow[PARENTHESIZED_EXPRESSION] = follow[PRIMARY]
            follow[REST_OF_OPTIONAL_METHOD_CALL] = follow[VAR_ACCESS_OR_MET_CALL]
            follow[CHAINED_MET_VAR] = REST_OF_REFERENCE.first + follow[REST_OF_REFERENCE]
            follow[REST_OF_CHAINING] = follow[CHAINED_MET_VAR]

            // <ArgsActuales> y relacionados
            follow[ACTUAL_ARGUMENTS] = follow[REST_OF_CHAINING] + follow[REST_OF_OPTIONAL_METHOD_CALL] + follow[CONSTRUCTOR_CALL] + follow[STATIC_METHOD_CALL]
            follow[OPTIONAL_EXPRESSION_LIST] = setOf(RIGHT_BRACKET)
            follow[FORMAL_ARGUMENTS] = OPTIONAL_BLOCK.first + follow[REST_OF_METHOD_DECLARATION] + BLOCK.first + follow[CONSTRUCTOR]
        }

    }

}

private operator fun Array<Set<TokenType>>.set(index: NonTerminal, value: Set<TokenType>) {
    this[index.ordinal] = value
}

private operator fun Array<Set<TokenType>>.get(nonTerminal: NonTerminal) =
    this[nonTerminal.ordinal]