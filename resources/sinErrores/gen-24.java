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
}

class D {
    int compute(int x) {
        return x * 2;
    }
}

class Main {
    static void main() {
        var a = new A();
        var result = a.getB().getC().getD().compute(21); // 21 * 2 = 42
        debugPrint(result);
    }
}