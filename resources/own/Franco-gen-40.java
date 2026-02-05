///---
///C_val: 3
///B_val: 2
///A_val: 1
///---
///C_val: 3
///B_val: 99
///A_val: 1
///---
///Soy C (polimorfico)
///C_val: 3
///B_val: 99
///A_val: 1
///--------------------

// Nivel 1
class A {
    int a_val;

    void setA(int v) {
        a_val = v; // Asigna a atributo de A
    }

    void imprimir() {
        System.printSln("Soy A");
    }
}

// Nivel 2
class B extends A {
    int b_val;

    void setB(int v) {
        b_val = v; // Asigna a atributo de B
    }

    void imprimir() {
        System.printSln("Soy B");
    }
}

// Nivel 3
class C extends B {
    int c_val;

    void setC(int v) {
        c_val = v; // Asigna a atributo de C
    }

    // Método que accede a atributos de TODOS los niveles
    void imprimirValores() {
        System.printS("C_val: ");
        System.printIln(c_val); // Atributo propio

        System.printS("B_val: ");
        System.printIln(b_val); // Atributo heredado de B

        System.printS("A_val: ");
        System.printIln(a_val); // Atributo heredado de A
    }

    // Sobrescribe imprimir()
    void imprimir() {
        System.printSln("Soy C (polimorfico)");
        imprimirValores();
    }
}

class Main {
    static void main() {
        var miC = new C();

        // 1. Asignar atributos de todos los niveles
        miC.setA(1); // Método de A
        miC.setB(2); // Método de B
        miC.setC(3); // Método de C

        // 2. Imprimir valores
        System.printSln("---");
        miC.imprimirValores();
        System.printSln("---");

        // 3. Modificar un valor heredado y reimprimir
        miC.setB(99);
        miC.imprimirValores();
        System.printSln("---");

        // 4. Prueba de Polimorfismo (Upcasting)
        var miB = miC;
        miB.imprimir(); // Debe llamar a C.imprimir()
        System.printSln("--------------------");
    }
}