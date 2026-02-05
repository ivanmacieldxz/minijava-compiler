///Animal dice: Soy un Animal!
///Perro dice: Guau!

class Animal {
    // Método que será sobrescrito
    void hacerRuido() {
        System.printSln("Soy un Animal!");
    }
}

class Perro extends Animal {

    // Sobrescribe (overrides) el método 'hacerRuido'
    void hacerRuido() {
        System.printSln("Guau!");
    }
}

class Main {

    // Este método prueba el polimorfismo.
    // Recibe un 'Animal', pero la llamada a 'a.hacerRuido()'
    // debe resolverse dinámicamente.
    static void imprimirRuido(Animal a) {
        a.hacerRuido(); // <--- ¡Esta es la llamada clave!
    }

    static void main() {
        var miAnimal = new Animal();
        var miPerro = new Perro();

        // 1. Prueba la versión base
        System.printS("Animal dice: ");
        imprimirRuido(miAnimal); // Llama a Animal.hacerRuido()

        // 2. Prueba la versión sobrescrita (polimorfismo)
        // Pasa un 'Perro' donde se espera un 'Animal'.
        System.printS("Perro dice: ");
        imprimirRuido(miPerro); // Debe llamar a Perro.hacerRuido()
    }
}