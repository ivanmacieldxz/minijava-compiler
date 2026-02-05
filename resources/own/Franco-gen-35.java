///20&30&40&50&60&70&80&exitosamente
//@Autor: Lau

class Nodo {
    int valor;
    Nodo izq;
    Nodo der;

    public Nodo(int v) {
        valor = v;
        izq = null;
        der = null;
    }
}

class Arbol {
    Nodo raiz;

    public Arbol() {
        raiz = null;
    }

    void insertar(int v) {
        if (raiz == null) {
            raiz = new Nodo(v);
        } else {
            insertarRec(raiz, v);
        }
    }

    void insertarRec(Nodo actual, int v) {
        if (v < actual.valor) {
            if (actual.izq == null) {
                actual.izq = new Nodo(v);
            } else {
                insertarRec(actual.izq, v);
            }
        } else {
            if (actual.der == null) {
                actual.der = new Nodo(v);
            } else {
                insertarRec(actual.der, v);
            }
        }
    }

    void imprimir() {
        imprimirRec(raiz);
    }

    void imprimirRec(Nodo actual) {
        if (actual != null) {
            imprimirRec(actual.izq);
            System.printIln(actual.valor);
            imprimirRec(actual.der);
        }
    }
}

class Main {
    static void main() {
        var arbol = new Arbol();

        arbol.insertar(50);
        arbol.insertar(30);
        arbol.insertar(20);
        arbol.insertar(40);
        arbol.insertar(70);
        arbol.insertar(60);
        arbol.insertar(80);

        arbol.imprimir();
    }
}