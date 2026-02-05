///20&exitosamente

class T4 {
    static void main() {
        var x = 0;
        if (1 == 2) {
            x = 10;
        } else {
            x = 20;
        }
        debugPrint(x);   // debería ser 20
    }
}
