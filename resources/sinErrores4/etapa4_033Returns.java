///[SinErrores]
class A {

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