///Imprimiendo lista:&10&20&30&--------------------&exitosamente

//@Autor: Lau

// 1. Clase 'Nodo'
// Representa un eslabón de la cadena.
class Nodo {
    int valor;
    Nodo siguiente; // Atributo de tipo clase

    public Nodo(int v) {
        valor = v;
        siguiente = null; // Prueba de asignación 'null'
    }

    // Getters y Setters
    Nodo getSiguiente() {
        return siguiente;
    }

    void setSiguiente(Nodo s) {
        siguiente = s;
    }

    int getValor() {
        return valor;
    }
}

// 2. Clase 'Lista'
// Administra los nodos.
class Lista {
    Nodo cabeza; // El 'head' de la lista

    public Lista() {
        cabeza = null;
    }

    // Agrega un elemento al FINAL de la lista
    void agregar(int v) {
        var nuevoNodo = new Nodo(v);

        // Caso 1: La lista está vacía
        if (cabeza == null) { // Prueba '== null'
            cabeza = nuevoNodo;
        }
        // Caso 2: La lista NO está vacía
        else {
            var actual = cabeza;
            // Recorrer hasta el último nodo
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            // Enganchar el nuevo nodo al final
            actual.setSiguiente(nuevoNodo);
        }
    }

    // Imprime todos los valores de la lista
    void imprimir() {
        var actual = cabeza;
        while (actual != null) { // Prueba '!= null'
            System.printIln(actual.getValor());
            actual = actual.getSiguiente();
        }
    }
}

// 3. Clase 'Main'
// Prueba la lista.
class Main {
    static void main() {
        var miLista = new Lista();

        miLista.agregar(10);
        miLista.agregar(20);
        miLista.agregar(30);

        System.printSln("Imprimiendo lista:");
        miLista.imprimir();

        System.printSln("--------------------");
    }
}