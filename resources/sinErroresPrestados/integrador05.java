///[SinErrores]
class Factory {
    Product make() {
        return new Product(10);
    }
}

class Product {
    int value;
    public Product(int v) {
        A.b().a = 5;
        A.a();
        value = v;
    }
}
