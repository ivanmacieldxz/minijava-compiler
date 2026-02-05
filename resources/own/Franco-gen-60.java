///42&exitosamente
class TestReturn {
    static int doble(int x) {
        return x + x;
    }

    static void main() {
        debugPrint(TestReturn.doble(21));
    }
}