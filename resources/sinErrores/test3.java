///33&exitosamente

class A{
    int met(){
        return 33;
    }
}

class Init{
    static void main(){
        Init.debugPrint((new A()).met());
    }
}