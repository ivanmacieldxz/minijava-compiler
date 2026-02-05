///1&1&1&exitosamente

class Init{
    static void main() {
        new Init().m1();
        var a = new Init();
        a.m1();
        a.m2().m1();
    }

    static Init m2() {
        return new Init();
    }

    void m1() {
        debugPrint(1);
    }
}
