///5&4&3&2&1&0&exitosamente
class TestWhile {
    static void main() {
        var x = 5;

        // Bucle while para contar hacia atrás desde 5 hasta 0
        while (x >= 0) {
            System.printIln(x); // Imprime el valor actual de x
            x = x - 1;          // Decrementa x
        }
    }
}