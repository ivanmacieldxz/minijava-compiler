///Debería entrar acá&También debería entrar acá&Acá termina&exitosamente

class Init extends System {

    static void main() {
        var bool = true;

        if (bool) {
            printSln("Debería entrar acá");
            if (!(!bool)) {
                printSln("También debería entrar acá");

                if (!bool) {
                    printSln("Acá no");
                } else if (bool)
                    if (bool)
                        if (!bool)
                            printSln("Aca tampoco");
                        else
                            printSln("Acá termina");
            } else {

            }
        } else {
            printSln("No deberia entrar acá");
        }
    }

}