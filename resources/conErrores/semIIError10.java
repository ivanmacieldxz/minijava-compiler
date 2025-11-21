///[Error:=|11]
// Tipos incompatibles incompatibles en la asignacion: B no conforma con C - ln: 11

class A {

    A v1;   
    
     void m1(B p1, C p2) {
        v1 = p2;

        p2 = p1;
        
    }
}
class B extends A {}
class C extends B {}


