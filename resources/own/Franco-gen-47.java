///123&exitosamente
class Objeto {
    int valor; // El atributo (x.valor)

    // Un método que devuelve un valor
    int metodo() {
        return 123;
    }
}

class Main {
    static void main() {
        var x = new Objeto();

        // --- EL CONFLICTO DE TRADUCCIÓN ---
        // Se usa el mismo objeto 'x' para la asignación y la llamada.

        x.valor = x.metodo();

        // ------------------------------------

        System.printIln(x.valor); // Debería imprimir 123
    }
}