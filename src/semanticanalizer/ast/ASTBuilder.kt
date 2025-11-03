package semanticanalizer.ast

import semanticanalizer.ast.member.Block
import symbolTable
import utils.Token

class ASTBuilder {

    var currentContext: ASTMember? = null
    lateinit var metVarName: Token
    var currentBlock: Block? = null
}

