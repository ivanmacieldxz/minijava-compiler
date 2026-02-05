///3&3&exitosamente

class A{
    void m1(int x,C c1){
        debugPrint(x);
        c1.m3();
    }
}

class C{
    void m3(){
        debugPrint(3);
    }
}

class Init{

    static void main()
    {
        var a = new A();
        var c = new C();
        var b = 3;
        a.m1(b,c);
    }
}