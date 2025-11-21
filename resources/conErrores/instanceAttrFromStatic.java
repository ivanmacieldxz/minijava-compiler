///[Error:x|12]
class A {
    static void x() {

    }
}

class B extends A {
    int x;
    static void y(int y) {
        //esto tiene que dar error porque estoy accediendo a un atributo de instancia
        x = 2;
    }
}