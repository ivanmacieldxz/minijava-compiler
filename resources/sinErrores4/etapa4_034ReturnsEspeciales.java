///[SinErrores]
class A {
    int x;
    void m(){
        var y=1+x+2;
        return;
    }
    int x(){
        return 2+3;
    }
    boolean b(){
        return true;
    }
    int h(){
        return x();
    }
}
class B extends A {
    A attA;
    B attB;
    A a(){
        return attA;
    }
    A n() {
        return attB;
    }
    static void main(){

    }

}