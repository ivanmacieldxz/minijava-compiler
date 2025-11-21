///[SinErrores]
class A{
    int a1;
    int a2;

    void m1(int p1){
        var c = new C();
        p1 = ++new C().mx().m2();
    }
    int m2(){
        return 10;
    }
}
class C{
    A mx(){
        return new A();
    }
}

class Init{
    static void main()
    { }
}
