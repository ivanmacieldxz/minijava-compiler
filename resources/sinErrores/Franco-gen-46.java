///10&20&exitosamente
class TestThis {
    int valor;

    // Constructor: usa 'this' para asignar un atributo
    public TestThis(int v) {
        this.valor = v;
    }

    // Método: usa 'this' para asignar un atributo
    void setValor(int v) {
        this.valor = v;
    }

    // Método: usa 'this' para leer un atributo
    int getValor() {
        return this.valor;
    }

    // Método: usa 'this' para llamar a otro método
    void imprimirValor() {
        // Llama a this.getValor()
        System.printIln(this.getValor());
    }
}

class Main {
    static void main() {
        var t = new TestThis(10);
        t.imprimirValor(); // Debería imprimir 10

        t.setValor(20);

        t.imprimirValor(); // Debería imprimir 20
    }
}