///0&exitosamente

class T2 {
    static void main() {
        var x = 0;
        if (2 < 1) {
            x = 5;
        }
        debugPrint(x);   // debería ser 0
    }
}
