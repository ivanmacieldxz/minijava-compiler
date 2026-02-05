///10122212398&exitosamente

class A{
    int b;
    int c;
    int d;

    void mc(){
        b = 0;
        while (b < 3){
            b = b + 1;
            debugPrint(b);
        }
        b = 10;
        while (b > 8){
            b = b - 1;
            debugPrint(b);
        }
    }

    void md(){
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
        a.md();
        a.mc();
    }
}


