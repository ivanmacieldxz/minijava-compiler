package semanticanalizer.ast

import semanticanalizer.ast.member.Sentence
import utils.Token

interface ASTMember {
    var parentSentence: Sentence?
    var token: Token
}