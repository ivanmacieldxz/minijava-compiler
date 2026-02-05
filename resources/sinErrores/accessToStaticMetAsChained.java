///funca&exitosamente

class Init extends System{
    static void main() {
        var instance = new Init();

        instance.x(new System());
        instance.y();
    }

    void x(System systemInstance) {
        systemInstance.printSln("funca");
    }

    void y() {
        this.printSln("funca");
    }


}