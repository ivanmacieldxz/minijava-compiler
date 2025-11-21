///[SinErrores]
class A {
    B a;
    void m() {
        a.b.c = 5;
        this.a.m1();
    }
}
class B {
    C b;
    void m1() {
        b.n();
    }
}
class C {
    int c;
    void n() {
        c = 10;
    }
}
class Init{
    static void main()
    { }
}