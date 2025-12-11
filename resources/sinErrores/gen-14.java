///3&3&3&3&exitosamente

class A{
    void m1(int x,int a,C c1,int z){
        debugPrint(x);
        debugPrint(a);
        debugPrint(z);
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
        a.m1(3,3,c,3);
    }
}