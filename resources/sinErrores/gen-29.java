///4000&1000&exitosamente
//

class A{

    int a1;

    void seta1(){
        a1 = 1000;
    }

    void mostrar2(){
        debugPrint(a1);
    }

}

class B extends A{
     int a4;

    void seta4(){
        a4 = 4000;
    }

    void mostrar(){
        debugPrint(a4);
    }
}


class Init{
    static void main()
    {
        var x = new B();
        x.seta4();//2
        x.mostrar();//3
        x.seta1();//0
        x.mostrar2();//2
    }
}


