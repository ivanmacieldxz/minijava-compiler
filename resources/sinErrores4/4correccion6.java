///[SinErrores]
class A {
    B a1;
    A v1;
    Object v2;
    String s2;

    void m1(B p1){
        v1= p1;
        a1=(p1);
        v1= new C();
        p1= null;
        v2= p1;
        v1=this;
    }

    void m2(){}

}
class C extends B {
}

class Init {
    static void main() {
    }
}

class B extends A{
}