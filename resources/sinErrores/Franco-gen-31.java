///Visito: 1&Visito: 2&Visito: 4&Visito: 3&Fin del recorrido&exitosamente
//@Autor: Lau

class NodoGrafo {
    int id;
    boolean visitado;

    // Para simular una lista de adyacencia sin arreglos,
    // cada nodo tendrá hasta 2 vecinos fijos.
    NodoGrafo vecino1;
    NodoGrafo vecino2;

    public NodoGrafo(int v) {
        id = v;
        visitado = false;
        vecino1 = null;
        vecino2 = null;
    }

    void conectar(NodoGrafo n1, NodoGrafo n2) {
        vecino1 = n1;
        vecino2 = n2;
    }

    void setVisitado(boolean b) {
        visitado = b;
    }

    boolean fueVisitado() {
        return visitado;
    }

    int getId() {
        return id;
    }
}

class Grafo {

    // Algoritmo DFS (Depth First Search)
    void dfs(NodoGrafo actual) {
        if (actual == null) {
            return; // Caso base: nodo nulo
        }

        // Chequear si ya visitamos este puntero (evitar ciclos)
        if (actual.fueVisitado()) {
            return;
        }

        // Marcar como visitado
        actual.setVisitado(true);

        System.printS("Visito: ");
        System.printIln(actual.getId());

        // Recursión a los vecinos
        dfs(actual.vecino1);
        dfs(actual.vecino2);
    }
}

class Main {
    static void main() {
        // Crear nodos
        var n1 = new NodoGrafo(1);
        var n2 = new NodoGrafo(2);
        var n3 = new NodoGrafo(3);
        var n4 = new NodoGrafo(4);

        // Conectar formando un "Diamante" con un ciclo de vuelta
        // 1 -> 2, 3
        // 2 -> 4
        // 3 -> 4
        // 4 -> 1 (Ciclo!)

        n1.conectar(n2, n3);
        n2.conectar(n4, null);
        n3.conectar(n4, null);
        n4.conectar(n1, null); // El ciclo peligroso

        var g = new Grafo();

        // Debería imprimir 1, 2, 4, 3 (o 1, 3, 4, 2 dependiendo del orden)
        // Y detenerse, NO entrar en bucle infinito.
        g.dfs(n1);

        System.printSln("Fin del recorrido");
    }
}