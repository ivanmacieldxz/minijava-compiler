// TEST CORRECTO – Todo debería ser aceptado

class A {
    int a1;
    B b1;

    public A() { }

    int m1(int x, int y) {
        return x + y;
    }

    B m2() {
        return new B();
    }
}

class B {
    int b2;
    C c1;

    public B() { }

    C m3() {
        return new C();
    }
}

class C {
    int c3;

    public C() { }

    int m4() {


        return 7;
    }
}

class Init {
    A a;
    B b;
    C c;
    int z;
    static void main() {


        a = new A();
        b = a.m2();           // Método válido
        c = b.m3();           // Método válido
        z = a.m1(1,2);        // Parámetros correctos

        z = a.m2().m3().m4(); // Encadenado de métodos correcto


        a.b1 = new B();       // Asignación válida


        z = new A().m2().m3().m4(); // Constructor encadenado correcto
    }
}
