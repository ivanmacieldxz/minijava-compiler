///3322&exitosamente

class A{
    public A(){
        debugPrint(33);
    }

    void mc(){
         var x = 10;
         var y = 12;
         var z = x + y;
         debugPrint(z);
    }
}


class Init{
    static void main()
    { 
        var a = new A();
        a.mc();
    }
}


