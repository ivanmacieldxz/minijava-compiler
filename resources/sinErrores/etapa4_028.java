///[SinErrores]
class C {
    int a;
    B x(){
        var variableChica=true; return new B();
    }
}
class B {
    A b;
    int metB(int arg){return 0;}
}
class A {
    C b;
    B x(){return new B();}
    static void main(){

    }
    int y(int a){
        var r=1; return 1;
    }
    int z(int argz) {
        var variable1 = x().metB(argz);
        return variable1;
    }

}

