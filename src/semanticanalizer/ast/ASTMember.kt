package semanticanalizer.ast

import semanticanalizer.ast.member.Sentence

interface ASTMember {
    var parentSentence: Sentence?
}