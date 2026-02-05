///10&exitosamente

class T3 {
    static void main() {
        var x = 0;
        if (1 == 1) {
            x = 10;
        }
        else {
            x = 20;
        }
        debugPrint(x);   // debería ser 10
    }
}
