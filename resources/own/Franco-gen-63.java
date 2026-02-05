///1&0&1&0&1&99&exitosamente
class TestWhileIfNum {
    static void main() {
        var x = 0;

        // Itera mientras x sea menor que 5
        while (x < 5) {

            // Imprime 1 si x es PAR, 0 si es IMPAR
            if ((x % 2) == 0) {
                System.printIln(1); // 1 = Par
            } else {
                System.printIln(0); // 0 = Impar
            }

            x = x + 1; // Incrementa el contador
        }

        System.printIln(99); // 99 = Bucle finalizado
    }
}