///5&true&a&exitosamente

class Init{
    static void main()
    {
        m1(1, true, 'a');
    }

    static void m1(int p1, boolean p2, char p3){
        p1 = 5;
        System.printIln(p1);
        System.printBln(p2);
        System.printCln(p3);
    }
}