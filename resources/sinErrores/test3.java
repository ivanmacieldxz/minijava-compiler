///33&exitosamente

class A{
    int met(){
        return 33;
    }
}

class Init{
    static void main(){
        debugPrint((new A()).met());
    }
}