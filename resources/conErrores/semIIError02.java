///[Error:=|11]
// Tipos incompatibles incompatibles en la asignacion: B no conforma con A - ln: 11

class A {

    A v1;   
    
    void m1(B p1)
    
    {
        p1 = v1;
        
    }
         
    

}

class B extends A {}


class Init{
    static void main()
    { }
}


