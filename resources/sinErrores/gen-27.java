///3&exitosamente

class A{
    void m1(C c1){
        c1.m3();
    }
}

class C extends A{
    void m3(){
        debugPrint(3);
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


