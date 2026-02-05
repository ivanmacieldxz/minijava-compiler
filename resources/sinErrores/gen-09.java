///33&33&exitosamente

class A{
    void mc(B b1){
        b1.mostrar();
        debugPrint(33);
    }
}

class B{
    void mostrar(){
        debugPrint(33);
    }
}


class Init{

    static void main()
    {
        var a = new A();
        var b1 = new B();
        a.mc(b1);
    }
}