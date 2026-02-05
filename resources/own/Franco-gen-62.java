///5&1&exitosamente
class A {
    static void main() {
        var x = 0; // Offset 0

        while (x < 1) {
            var y = 5; // Offset -1 (en el mismo RA del método)
            System.printIln(y);
            x = x + 1;
        } // Fin del bloque del while

        System.printIln(x); // 'x' sigue siendo accesible
    }
}