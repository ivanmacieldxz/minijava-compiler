package semanticanalizer.ast

import utils.Token

class ASTBuilder {

    var currentContext: ASTMember? = null
    lateinit var metVarName: Token

    //TODO: método para añadir hijo al padre solo si este no tiene return, sino tirar exc
}

