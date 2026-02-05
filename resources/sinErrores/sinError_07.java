///false&true&exitosamente

class Init7{
    static void main()
    {
        m1(true);
    }

    static void m1(boolean p1){
        p1 = false;
        System.printBln(p1);
        p1 = !p1;
        System.printBln(p1);
    }
}