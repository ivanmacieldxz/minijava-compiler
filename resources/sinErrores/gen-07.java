///33&exitosamente

class D {
    E getE() {
        return new E();
    }
}

class E {
    int getInt() {
        return 33;
    }
}


class Init{
    static void main()
    {
        var x = new D();
        var y = x.getE().getInt();
        debugPrint(y);
    }
}

