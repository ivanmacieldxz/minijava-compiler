package utils


enum class TokenType: SyntacticStackable {

    EOF,
    LEFT_BRACKET,
    RIGHT_BRACKET,
    LEFT_CURLY_BRACKET,
    RIGHT_CURLY_BRACKET,
    SEMICOLON,
    COMMA,
    COLON,
    DOT,
    MODULUS,
    MULTIPLICATION,
    DIVISION,
    CLASS_IDENTIFIER,
    MET_VAR_IDENTIFIER,
    INTEGER_LITERAL,
    CHAR_LITERAL,
    STRING_LITERAL,
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL,
    LESS_THAN,
    LESS_THAN_OR_EQUAL,
    ASSIGNMENT,
    EQUALS,
    DIFFERENT,
    NOT,
    AND,
    OR,
    ADDITION,
    INCREMENT,
    SUBSTRACTION,
    DECREMENT,
    EXTENDS,
    CLASS,
    PUBLIC,
    STATIC,
    VOID,
    BOOLEAN,
    CHAR,
    INT,
    ABSTRACT,
    FINAL,
    IF,
    ELSE,
    WHILE,
    RETURN,
    VAR,
    THIS,
    NEW,
    NULL,
    TRUE,
    FALSE,
    ;

    override fun toString(): String =
        when (this) {
            EOF -> "end of file"
            LEFT_BRACKET -> "("
            RIGHT_BRACKET -> ")"
            LEFT_CURLY_BRACKET -> "{"
            RIGHT_CURLY_BRACKET -> "}"
            SEMICOLON -> ";"
            COMMA -> ","
            COLON -> ":"
            DOT -> "."
            MODULUS -> "%"
            MULTIPLICATION -> "*"
            DIVISION -> "/"
            CLASS_IDENTIFIER -> "identificador de clase"
            MET_VAR_IDENTIFIER -> "identificador de método o variable"
            INTEGER_LITERAL -> "literal entero"
            CHAR_LITERAL -> "literal char"
            STRING_LITERAL -> "literal string"
            GREATER_THAN -> ">"
            GREATER_THAN_OR_EQUAL -> ">="
            LESS_THAN -> "<"
            LESS_THAN_OR_EQUAL -> "<="
            ASSIGNMENT -> "="
            EQUALS -> "=="
            DIFFERENT -> "!="
            NOT -> "!"
            AND -> "&&"
            OR -> "||"
            ADDITION -> "+"
            INCREMENT -> "++"
            SUBSTRACTION -> "-"
            DECREMENT -> "--"
            EXTENDS -> "extends"
            CLASS -> "class"
            PUBLIC -> "public"
            STATIC -> "static"
            VOID -> "void"
            BOOLEAN -> "boolean"
            CHAR -> "char"
            INT -> "int"
            ABSTRACT -> "abstract"
            FINAL -> "final"
            IF -> "if"
            ELSE -> "else"
            WHILE -> "while"
            RETURN -> "return"
            VAR -> "var"
            THIS -> "this"
            NEW -> "new"
            NULL -> "null"
            TRUE -> "true"
            FALSE -> "false"
        }
    
}