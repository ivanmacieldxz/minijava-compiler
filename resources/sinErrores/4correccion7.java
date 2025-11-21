///[Error:p1|6]
class A{
    B a1;
    A a2;
    void m1(){
        var p1 = new A();
        p1.a1.p1 = 4;
    }

}
class B{
    int p1;
}

class Init {
    static void main() {
    }
}