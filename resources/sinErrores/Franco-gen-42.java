///3&6&9&12&15&exitosamente
class Counter {
    int count; // Atributo (prueba 'this' implícito)

    // Método que modifica un atributo (this.count)
    void increment() {
        count = count + 3; // Prueba 'generar' y 'generarParaAlmacenar' implícitos
    }

    // Método que lee un atributo (this.count)
    int getCount() {
        return count; // Prueba 'generar' implícito
    }
}

class Main {
    static void main() {
        var c = new Counter();

        // Prueba un 'while' que llama a un método
        while (c.getCount() < 15) {
            c.increment();
            System.printIln(c.getCount()); // Imprime el estado
        }
    }
}