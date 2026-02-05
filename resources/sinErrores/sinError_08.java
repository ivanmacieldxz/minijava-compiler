///0&1&1&0&exitosamente

class Init8{
    static void main()
    {
        m1(0, false);
    }

    static void m1(int p1, boolean p2){
        System.printIln(p1);
        ++p1;
        System.printIln(p1);

        System.printIln(p1);
        --p1;
        System.printIln(p1);
    }
}