///[SinErrores]
class A{
    int a1;
    B a2;
    void m1(int p1){
        a1 = m2(p1+(a1-10)+2, a2, a2);
    }
    int m2(int p2, B p3, A p4){
        return 10;
    }
}
class B extends A{
    int b1;
    void m3(){
        a1 = 20;
    }
}
class Init{
    static void main(){
    }
}