///33&exitosamente

class A{
  
  
    
      void m1(int p1){
        debugPrint(p1);
    }
    
      int m2(){
        
        return 33;
    }
}


class Init{
    static void main()

    { 
        
        var x = new A();
        x.m2();        
        debugPrint(x.m2());
    }
}


