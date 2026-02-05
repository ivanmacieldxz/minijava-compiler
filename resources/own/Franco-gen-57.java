///15&exitosamente
class TestWhileReturn {
    static int sumaHasta(int n) {
        var acc = 0;
        while (n > 0) {
            acc = acc + n;
            n = n - 1;
        }
        return acc;
    }

    static void main() {
        debugPrint(TestWhileReturn.sumaHasta(5)); // → 15
    }
}