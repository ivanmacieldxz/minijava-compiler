///10&20&10&20&exitosamente

class A {
    int x;

    public A() {
        x = 10;
    }

    int getX() {
         return x;
    }
}

class B extends A {
    int x;

    public B() {
        x = 20;
    }
}

class Init{
    static void main() {
        var a = new A();
        var b = new B();

        //System.printSln("a.x");
        System.printIln(a.x);
        //System.printSln("b.x");
        System.printIln(b.x);
        //System.printSln("a.getX()");
        System.printIln(a.getX());
        //System.printSln("b.getX()");
        System.printIln(b.getX());

    }
}
