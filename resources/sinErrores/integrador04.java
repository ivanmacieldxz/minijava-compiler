///[SinErrores]
class Loop {
    void run(int n) {
        var i = 0;
        while (i < n) {
            if ((i % 2) == 0) {
                printEven(i);
            } else {
                printOdd(i);
            }
            i = i + 1;
        }
    }

    void printEven(int x) { }
    void printOdd(int x) { }
    static void main()
    { }
}
