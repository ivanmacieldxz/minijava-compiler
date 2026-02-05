///1&exitosamente

class Init extends A {
    static void main() {
        var a = new A();

        debugPrint(a.x().c.x());
    }
}

class A {
    B b;

    public A() {
        b = new B();
    }

    static B x() {
        return new B();
    }
}

class B extends A {
    C c;

    public B() {
        c = new C();
    }
}

class C {

    int x() {
        return 1;
    }
}