///[SinErrores]
abstract class A{
    abstract void m1();
    static void m2()
    {}
    abstract void m3(int p1, String p2);
    abstract void m4(int p3, boolean p4);
}
abstract class B extends A{
    abstract void m5();

}


class X extends B{
    void m1(){}
    static void main(){

    }
    void m3(int p1, String p2){}
    void m4(int p3, boolean p4){}
    void m5(){}

}