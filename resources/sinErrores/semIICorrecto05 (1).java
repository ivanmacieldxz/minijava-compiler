// Prueba un lado izquierdo con dos encadenamiento y atributos heredados

class A {
    B a1;
    int a2;
   
    
    
    
} 

class B extends A{
    A a3;
    
     void m1(B p1)     
    {
        a1.a3.a2 = 4;
        
    }
}


class Init{
    static void main()
    { }
}


