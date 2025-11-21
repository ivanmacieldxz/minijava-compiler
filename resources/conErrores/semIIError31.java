///[Error:metodoQueNoExiste|33]
// Tipo de parámetros incorrectos
class A{
    int a1;
    B b1;
    public A(){}

    int m1(int x, int y){return x+y;}

    B m2(){return new B();}
    void metodoVoid(){}
}
class B{
    int b2;
    C c1;
    public B(){}

    C m3(){return new C();}

}
class C{
    int c3;
    public C(){}

    int m4(){return 7;}
}

class Init{
    A a;
    int x;
    void x(){
        a = new A();
       new A().metodoQueNoExiste();
    }
}