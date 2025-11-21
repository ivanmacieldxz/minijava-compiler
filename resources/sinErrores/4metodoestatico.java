///[SinErrores]

class A {
    int a;
    B b;
    void m(){
        B.a();
    }
    B a(){
        return new B();
    }
    static void main(){

    }

}
static class B {
    int a;
    static void a() {

    }
}