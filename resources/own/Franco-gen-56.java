///123&456&exitosamente
class Widget {
    int id;

    // Constructor: recibe un valor y lo asigna al atributo 'id'
    public Widget(int val) {
        id = val;
    }

    void imprimirId(){
        System.printIln(id);
    }
}

class TestNew {
    static void main() {
        var miWidget = new Widget(123);
        miWidget.imprimirId();

        System.printIln(456);
    }
}