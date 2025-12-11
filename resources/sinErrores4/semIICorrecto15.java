// Prueba un lado izquierdo simple

class A {
    int a1;
    B b1;


    void m1(){
        this.a1 = 5;
       this.b1.charB = 'a';
        this.b1.c1.boolB = false;
    }
}    
class B extends A {
    char charB;
    C c1;
}

class C extends B{
    boolean boolB;
}


class Init{
    static void main()
    { }
}
