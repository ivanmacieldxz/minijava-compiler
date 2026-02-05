///4&2&1&2&20&exitosamente

class Init{
    static void main()
    {
        m1(0);
    }

    static void m1(int p){
        p = 2 + 2;
        System.printIln(p);
        p = p / 2;
        System.printIln(p);
        p = 5 % 2;
        System.printIln(p);
        p = p * 2;
        System.printIln(p);

        System.printIln(10+10);
    }
}