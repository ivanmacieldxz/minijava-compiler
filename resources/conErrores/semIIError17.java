///[Error:return|8]

class A {
    int a1;
    B b1;
    C c1;
    void m1(){
        return 1;
    }

}
class B extends A {}

class C extends B{}



class Init{
    static void main()
    { }
}