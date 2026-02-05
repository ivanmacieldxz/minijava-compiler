///Ida: 10 20 30&Vuelta: 30 20 10&exitosamente
//@Autor: Lau

class Node {
    int val;
    Node next;
    Node prev; // Nuevo atributo: Referencia hacia atrás

    public Node(int v) {
        val = v;
        next = null;
        prev = null; // Inicialización vital
    }
}

class DList {
    Node head;
    Node tail; // Puntero al último, para recorrer al revés

    public DList() {
        head = null;
        tail = null;
    }

    // Agregar al final (mantiene next y prev)
    void add(int v) {
        var nuevo = new Node(v);

        if (head == null) {
            head = nuevo;
            tail = nuevo;
        } else {
            // Enlazar el final actual con el nuevo
            tail.next = nuevo;
            nuevo.prev = tail; // Enlace hacia atrás

            // Actualizar el tail
            tail = nuevo;
        }
    }

    // Recorrer usando 'next'
    void imprimirIda() {
        var p = head;
        System.printS("Ida: ");
        while (p != null) {
            System.printI(p.val);
            System.printS(" ");
            p = p.next;
        }
        System.printSln(" ");
    }

    // Recorrer usando 'prev' (Prueba de fuego para los offsets)
    void imprimirVuelta() {
        var p = tail;
        System.printS("Vuelta: ");
        while (p != null) {
            System.printI(p.val);
            System.printS(" ");
            p = p.prev; // Acceso al tercer atributo
        }
        System.printSln(" ");
    }
}

class Main {
    static void main() {
        var lista = new DList();

        lista.add(10);
        lista.add(20);
        lista.add(30);

        // 1. Prueba de punteros 'next' y offsets básicos
        lista.imprimirIda();

        // 2. Prueba de punteros 'prev' y offsets nuevos
        // Si la clase Node quedó "sucia" de un test anterior,
        // esto fallará porque 'prev' no existía o tenía otro offset.
        lista.imprimirVuelta();
    }
}