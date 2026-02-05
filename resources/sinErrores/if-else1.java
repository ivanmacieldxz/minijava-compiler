///Entro al else, como se esperaba&exitosamente

class Init extends System {
    static void main() {
        var x = true;

        if (!x) {
            printSln("no debería entrar al if");
        } else
            printSln("Entro al else, como se esperaba");
    }
}