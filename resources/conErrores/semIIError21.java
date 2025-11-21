///[Error:=|9]

class A {
    int a1;
    B b;

    void m2(){
        a1 = b.b1;
        b.charB1 = a1;
    }

}
class B extends A {
    int b1;
    char charB1;

    void m1() {

    }
}


class Init{
    static void main()
    { }
}
