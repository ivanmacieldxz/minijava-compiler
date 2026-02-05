///7&10&false&exitosamente

class Init3{
    static void main()
    {
        m1(7, true);
    }

    static void m1(int p1, boolean p2){
        System.printIln(p1);
        p1 = 10;
        p2 = p1 == 5;
        System.printIln(p1);
        System.printBln(p2);
    }
}