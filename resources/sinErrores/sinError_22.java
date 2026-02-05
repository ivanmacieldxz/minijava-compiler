///7&exitosamente

class T5 {
    static void main() {
        var x = 0;
        if (1 < 2) {
            if (2 < 3) {
                x = 7;
            }
        }
        debugPrint(x);   // debería ser 7
    }
}
