///[Error:ConcreteClass|7]
abstract class Base {
    abstract void mustImplement();
    abstract int another();
}

class ConcreteClass extends Base {
    // ERROR: falta implementar 'another()'
    void mustImplement() {
        // implementación
    }
}
