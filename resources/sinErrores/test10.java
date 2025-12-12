///5&exitosamente

class A{
    B b;
    public A(B b){
        this.b = b;
    }
    int met(){
        return 5;
    }
}

class B{
    C c;
    public B(C c){
        this.c = c;
    }
}

class C{
    int met(){
        return 5;
    }
}

class Init{
    static void main(){
        debugPrint(
            (
                new A(
                    new B(
                        new C()
                    )
                )
            )
            .b
            .c
            .met()
        );
    }
}