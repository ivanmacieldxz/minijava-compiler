///1&1&1&exitosamente

class Init{
    static void main() {
        m1();
        Init.m1();
        new Init().m1();
    }

    static void m1() {
        debugPrint(1);
    }
}
