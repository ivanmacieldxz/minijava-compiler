///antes de return&exitosamente

class Init {
    static void main() {
        new Init().x();
    }

    int x() {
        System.printSln("antes de return");
        return 1;
        System.printSln("después de return");
    }
}