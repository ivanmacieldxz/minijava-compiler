///true&exitosamente

class A{
    B b;
    public A(){
        b = null;
    }
}

class B{

}

class Init{
    static void main(){
        var a = new A();
        System.printB(a.b == null);
    }
}