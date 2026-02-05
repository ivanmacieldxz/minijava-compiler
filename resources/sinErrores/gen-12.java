///33&4&6&exitosamente

class A{
    void m1(){
        var b1 = new B();
        var x = 3+3;
        b1.m2(30+3,2*2,x);
    }
}

class B{
    void m2(int x,int a,int b){
        debugPrint(x);
        debugPrint(a);
        debugPrint(b);
    }
}

class Init{

    static void main()
    {
        var a = new A();
        a.m1();
    }
}