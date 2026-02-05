///123&exitosamente
class Objeto {
    int valor; // El atributo que asignaremos
}

class Test {

    // Un método estático que devuelve un nuevo objeto
    static Objeto m() {
        return new Objeto();
    }

    static void main() {

        // --- PRUEBA 1: Constructor.generarParaAlmacenar() ---
        // Esta línea prueba: new Objeto().valor = 10;
        // El 'new Objeto()' es el NodoLlamadaConstructor a la izquierda.
        // El objeto se pierde (sin GC), pero el código debe generarse
        // y ejecutarse sin errores.
        new Objeto().valor = 10;


        // --- PRUEBA 2: Metodo.generarParaAlmacenar() ---
        // Esta línea prueba: Test.m().valor = 20;
        // 'Test.m()' es el NodoLlamadaMetodo a la izquierda.
        Test.m().valor = 20;


        // --- PRUEBA 3: Verificación (para asegurar que la VM sigue viva) ---
        // Esto prueba la asignación simple que ya funciona.
        var x = new Objeto();
        x.valor = 123;
        System.printIln(x.valor); // Debe imprimir 123
    }
}