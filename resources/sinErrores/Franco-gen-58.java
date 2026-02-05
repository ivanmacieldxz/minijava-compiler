///107&exitosamente
class TestIfReturn {
    static int max(int a, int b) {
        if (a > b)
            return a;
        return b;
    }

    static void main() {
        debugPrint(TestIfReturn.max(10, 3));   // → 10
        debugPrint(TestIfReturn.max(2, 7));    // → 7
    }
}