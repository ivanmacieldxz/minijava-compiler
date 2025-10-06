package semanticanalizer

abstract class SemanticException(override val message: String): Exception(message)

class RepeatedDeclarationException(override val message: String): SemanticException(message)