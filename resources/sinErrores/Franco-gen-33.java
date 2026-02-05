///500Valor final: &1000&exitosamente
//@Autor: Lau

class Nivel3 {
    int valor;

    void setValor(int v) {
        valor = v;
    }

    int getValor() {
        return valor;
    }
}

class Nivel2 {
    Nivel3 interno;

    public Nivel2() {
        interno = new Nivel3();
    }

    Nivel3 getInterno() {
        return interno;
    }
}

class Nivel1 {
    Nivel2 medio;

    public Nivel1() {
        medio = new Nivel2();
    }
}

class Main {
    static void main() {
        var base = new Nivel1();

        base.medio.getInterno().valor = 500;

        debugPrint(base.medio.getInterno().valor);

        base.medio.interno.valor = 1000;

        System.printS("Valor final: ");
        System.printIln(base.medio.getInterno().getValor());
    }
}