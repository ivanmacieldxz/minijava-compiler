///[SinErrores]
class X {
    int a1;
}
class A extends X {

    B a2;
    int a1;

    static void x() {}
    void m1(int p1){
        a1 = m2(p1+(a1-10)+2, a2, a2);
    }
    static void y() {}
    int m2(int p2, B p3, A p4){
        return 10;
    }
}
class B extends A{
    int b1;
    int a1;

    void m3(){
        a1 = 20;
    }
    static void b() {}
    int m2(int p2, B p3, A p4){
        return 10;
    }
}
class Init{
    static void main(){
    }
}

class C {

    int m1() {

    }

    void m2() {
        m1();
    }

}

class D extends C {
    int m1() {

    }

    void m3() {
        m1();
    }
}