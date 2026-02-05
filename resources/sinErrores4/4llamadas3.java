///[SinErrores]
class C{
    int a;
    void m1(){
        a = 1;
        var b = 2;
        var c = a;
        x(2);
        x(2).y();
x(2).y().z(3);
        g().x(2);
        var u = x(3).w;
        A.a1();
    }
    A x(int i){
        return new A();
    }
    C g(){
        return this;
    }
}
class A{
    int w;
    static void main(){

    }

    static void a1(){

    }
    B y(){
        return new B();
    }
}
class B{
    boolean z(int y){
        return true;
    }
}