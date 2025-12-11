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
class B {
    int a;
    static void a() {

    }
}