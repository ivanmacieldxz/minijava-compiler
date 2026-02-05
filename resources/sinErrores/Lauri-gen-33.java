///101222&exitosamente

class A{
    int b;
    int c;
    int d;

    void mc(){
        var x = 10;
        if(x==10){
            debugPrint(x);
        }
        var y = 12;
        if(y==12){
            debugPrint(y);
        }
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


