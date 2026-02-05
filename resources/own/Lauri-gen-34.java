///12398&exitosamente

class A{
    int b;
    int c;
    int d;

    void md(){
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

}


class Init{
    static void main()
    {
        var a = new A();
        a.md();
    }
}


