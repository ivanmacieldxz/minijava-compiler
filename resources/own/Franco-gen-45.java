///1234&33&exitosamente

class A{
    int x;
    
   
      void mc(){
        debugPrint(1234);
        x = 33;
        debugPrint(x);
      }
}


class Init{
    static void main()
    { 
        var a = new A();
        a.mc();
        
    }
}


