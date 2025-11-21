///[SinErrores]
class A {
    int x;
    int a;
    X x(){
    return new X();
    }
}
class X {
    B attX;
}

class B extends A {
    int c;
    A attB;
    A a(X x,B b){
        var var1= x.attX.a(x,b);
        var var2=var1;
        attB=var2;
        attB=var1;
        return this;
    }
    static void main(){

    }
    int fib(int f){
        return fib(f-1)+fib(f-2);
    }
}