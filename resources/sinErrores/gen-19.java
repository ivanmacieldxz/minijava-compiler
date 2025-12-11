///33&33&exitosamente

class A{

    int x;

    void m1(C c1){
        x = 33;
        debugPrint(x);
        c1.m2(x);
    }
}

class C{
    void m2(int c){
        debugPrint(c);
    }
}

class Init{
    static void main()
    {
        var a = new A();
        var c = new C();
        a.m1(c);
    }
}

