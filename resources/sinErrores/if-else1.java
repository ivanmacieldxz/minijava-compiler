///Entro al else, como se esperaba&exitosamente

class Init extends System {
    static void main() {
        var x = true;

        if (!x) {
            System.printSln("no debería entrar al if");
        } else
            System.printSln("Entro al else, como se esperaba");
    }
}