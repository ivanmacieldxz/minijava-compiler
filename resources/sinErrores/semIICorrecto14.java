// Prueba un lado izquierdo simple

class A {
    int a1;
    B b1;
    C c1;
    void m1(){
        return ;
    }
    int m2(){
        return a1;
    }
    int m3(){
        return 5;
    }
    boolean m4(){
        return true;
    }
    char m5(){
        return 'a';
    }
    B m6(){
        return b1;
    }
    //CASO RE IMPORTANTeE
    B m7(){
        return c1;
    }
}    
class B extends A {}

class C extends B{}


class Init{
    static void main()
    { }
}
