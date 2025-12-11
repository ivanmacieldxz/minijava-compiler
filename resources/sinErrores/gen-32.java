///11&exitosamente
// 

class A{

    static void m2(){
        debugPrint(1);
    }

}

class B {
    static void m2(){
        debugPrint(1);
    }
}


class Init{
    static void main()
    
    {
        
        var x = new A();
        var y = new B();
        x.m2();
        y.m2();
    }
}


