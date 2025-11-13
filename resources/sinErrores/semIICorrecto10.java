// Acceso simple a una variable de instancia
// Chequea el uso de operadores unarios de incremento

class A {
    boolean b1;
    boolean b2;

    B be;
    B be2;
     void m1(){
         b1 = true;
         b1 = 3 >= 4;
         b2 = ((4 > 5) || (6 > 7));
         b2 = ((4 > 5) && (6 > 7));
         b2 = 3 != 5;
         b2 = 2 == 2;

        b1 = be == be2;
         b1 = be != be2;
    }
    

}


class B extends A{
    
}


class Init{
    static void main()
    { }
}


