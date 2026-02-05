///true&5&exitosamente

class A{
    B b;
    int c;
    public A(int b){
        this.b = null;
        c = b;
    }
}

class B{

}

class Init{
    static void main(){
        var a = new A(5);
        System.printBln(a.b == null);
        System.printIln(a.c);
    }
}