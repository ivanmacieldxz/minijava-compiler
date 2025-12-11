// Probando llamadas a metodos estaticos
class A {
    int a2;

    static void m1(){}
    void m2(){
        A.m1();
    }
} 

class Init{
    static void main()
    { }
}


