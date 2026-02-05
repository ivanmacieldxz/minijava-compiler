///33&exitosamente

class A {
    int m1() {
        return 33;
    }
}

class Init {
    static void main() {
        var x = new A();
        var y = x.m1();
        debugPrint(y);
    }
}