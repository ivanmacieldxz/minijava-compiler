///[SinErrores]
class B {
    C a;
}
class C {
    D b;
}
class A {
    B x;
    void m(){
        var y= x.a.b;
    }
    static void main(){

    }
}
class D {}