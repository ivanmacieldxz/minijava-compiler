///7&exitosamente

class D {
    int u;
    public D(){
        u=7;
    }
    E getE() {
        return new E();
    }
}

class E {
    D w;
    public E(){
        w = new D();
    }
    int getInt() {

        return 33;
    }
}


class Init{
    static void main()
    {
        var x = new E();
        var y = x.w.u;
        debugPrint(y);
}
}