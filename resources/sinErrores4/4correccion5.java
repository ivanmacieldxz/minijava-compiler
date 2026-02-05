///[SinErrores]
class A{
    void m1(){

        B.m1();
        Object.debugPrint(2);
        B.m2("nuko");
    }
}
class B{
    static void m2(String s){
    }
    static void m1(){

    }
}

class Init{
    static void main(){
    }
}