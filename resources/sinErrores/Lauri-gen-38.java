///9&exitosamente

class A{
    int getInt(){
        return 5;
    }
}
class Init{
    static void main()
    {
        var a = new A();
        var x = (4+a.getInt())*6 - (10/2); //  como no hay precedencia de operadores, no computa bien sin parentesis
        debugPrint(x);
    }
}