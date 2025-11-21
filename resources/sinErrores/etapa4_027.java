///[SinErrores]
class B {
    int a;
    int metB(boolean t){return 0;}
}
class A {
    B b;
    void x(){
        var y=10;
    }
    static void main(){

    }
    int z(){return 0;}
    void y(int arg1){

        var i=b.a+arg1;
        var x=b;
        var z=x.a+4+z();
        var w= x.metB(true)+x.metB(false);
    }
}


