///12&exitosamente

class Init extends A {
    static void main() {
        var a = new A();

        a.b.c = new A().createB().c;

        debugPrint(a.b.c.testField);
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

    B createB() {
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

    int testField;

    public C() {
        testField = 12;
    }

    int x() {
        return 1;
    }
}