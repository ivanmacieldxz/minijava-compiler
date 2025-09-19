package utils

import utils.SyntacticStackable

enum class NonTerminal: SyntacticStackable {
    INITIAL,
    CLASS,
    CLASS_LIST,
    OPTIONAL_MODIFIER,
    OPTIONAL_INHERITANCE,
    MEMBER_LIST,
    MEMBER,
    TYPE,
    REST_OF_MEMBER_DECLARATION,
    REST_OF_METHOD_DECLARATION,
    MODIFIER,
    METHOD_TYPE,
    CONSTRUCTOR,


}