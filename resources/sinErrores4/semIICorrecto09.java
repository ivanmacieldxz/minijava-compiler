//Probando encadenados.

class A {
    B a1;

    int a2;

    static void main() {

    }
}

class B extends A{
    A a3;

    void m1(B p1)
    {
        a1.a3.a2 = 4;
        a1.m2();
    }

    A m2() {

    }
}