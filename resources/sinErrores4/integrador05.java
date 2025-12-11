///[SinErrores]
class Factory {
    Product make() {
        return new Product(10);
    }
}

class Product {
    int value;
    public Product(int v) {
        A.a();
        A.b().a = 5;
        value = v;
    }
}

class A {

    static A b() {
        return new A();
    }

    static void a() {
    }
    int a;
    static void main()
    { }

}
