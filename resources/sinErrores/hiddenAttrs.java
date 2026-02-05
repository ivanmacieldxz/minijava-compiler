///10&false&exitosamente

class Init extends System {
    static void main() {
        printIln(new A().x);
        printBln(new B().x);
    }
}

class A {
    int x;

    public A() {
        x = 10;
    }
}

class B {
    boolean x;

    public B() {
        x = false;
    }
}