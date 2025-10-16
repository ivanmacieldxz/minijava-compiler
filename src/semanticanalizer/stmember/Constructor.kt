package semanticanalizer.stmember

import utils.Token
import utils.Token.DummyToken
import java.util.Collections

class Constructor(
    override var token: Token = DummyToken,
    override val parentClass: Class
) : ClassMember, Callable {

    override var paramMap: MutableMap<String, FormalArgument> = Collections.emptyMap()
    override var declarationCompleted = false

    override fun toString(): String {
        return paramMap.toString() + "\n"
    }

    override fun isWellDeclared() {
        paramMap.values.forEach {
            it.isWellDeclared()
        }
    }

    fun isDefaultConstructor(): Boolean {
        return token == DummyToken
    }

}