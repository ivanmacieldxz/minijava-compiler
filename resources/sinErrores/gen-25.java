///hola&exitosamente

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
    String compute() {
        return "hola";
    }
}

class Main {
    static void main() {
        var a = new A();
        var result = a.getB().getC().getD().compute();
        System.printSln(result);
    }
}