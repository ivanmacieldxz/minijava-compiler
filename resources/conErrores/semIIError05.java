///[Error:x|17]
// Nombre de variable local o parametro repetido x - ln: 17
class A {
    int a1;

    void m1(int p1)
    {
         var x = 1; 
       
        {
            {
                var y = 2;
            }
            var y = 3;

           {
                 var x = true;
           }
           
           
        }
        
       
    }
    
 
  
}




class Init{
    static void main()
    { }
}


