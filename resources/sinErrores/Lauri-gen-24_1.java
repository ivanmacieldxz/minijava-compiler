///1&exitosamente

class A {
    B getB() {
        return new B();
    }

}

class B {
    C getC() {
        return new C();
    }
    int compute(int x){
    //    var r = x+1;
        return 1;
    }
    int getInt(){
        return 1;
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
        var b = a.getB();
        var i = b.compute(4);
        debugPrint(i);
}
}