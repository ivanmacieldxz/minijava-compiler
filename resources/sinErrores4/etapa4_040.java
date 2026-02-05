///[SinErrores]
class A {
    int x;
    int a;
    A m(){ return new A(2);}
    A attRec;
    public A(int x){

        attRec.a=4;
        var obj= new A(2);
    //    A.a=4;
        this.x = x;
     //   attRec.this.a=x;
    }

    A arr() {
        return this;
    }
    static void main(){

    }

}