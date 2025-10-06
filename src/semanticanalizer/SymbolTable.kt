package semanticanalizer

class SymbolTable {

    val classList = mutableSetOf<Class>()
    var currentClass: Class = DummyClass
    var currentContext: Declarable = DummyContext

    fun checkStatements() {

    }

    fun consolidate() {

    }

}