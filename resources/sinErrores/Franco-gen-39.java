///---
///Nombre: Fido
///Edad: 4
///---
///Nombre: Luna
///Edad: 2
///--------------------

class Mascota {
    String nombre;
    int edad;

    // --- Constructor con Múltiples Parámetros ---
    public Mascota(String n, int e) {
        nombre = n;
        edad = e;
    }

    String getNombre() {
        return nombre;
    }

    int getEdad() {
        return edad;
    }
}

class Main {
    static void main() {
        // 1. Prueba la llamada al constructor
        var perro = new Mascota("Fido", 4);

        // 2. Verifica los valores
        System.printS("Nombre: ");
        System.printSln(perro.getNombre());
        System.printS("Edad: ");
        System.printIln(perro.getEdad());

        // 3. Prueba otra instancia
        var gato = new Mascota("Luna", 2);

        System.printSln("---");
        System.printS("Nombre: ");
        System.printSln(gato.getNombre());
        System.printS("Edad: ");
        System.printIln(gato.getEdad());

        System.printSln("--------------------");
    }
}