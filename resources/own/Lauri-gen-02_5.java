///1234&37&48&11&59&118&exitosamente

class A{
    int x;
    int y;
    int z;


    void mc(){
        debugPrint(1234);
        x = 32+5;
        debugPrint(x);
        y= 11+x;
        debugPrint(y);
        x= 10+1;
        debugPrint(x);
        z= y+x;
        debugPrint(z);
        z=2*z;
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


