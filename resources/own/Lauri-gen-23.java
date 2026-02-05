///42&exitosamente

class A {
    B getB() {
        return new B();
    }
}

class B {
    C getC() {
        return new C();
    }
}

class C {
    D getD() {
        return new D();
    }
    int getInt() {
        return 42;
    }
}

class D {
    E getE() {
        return new E();
    }
}

class E {
    int getInt() {
        return 42;
    }
}

class Main {
    static void main() {
        var a = new A();
        var result = a.getB().getC().getInt();
        debugPrint(result);
    }
}