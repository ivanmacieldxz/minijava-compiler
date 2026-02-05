///5&5&9&54&9&exitosamente

class A{
    int getInt(){
        return 5;
    }
}
class Init{
    static void main()
    {
        var a = new A();
        var x = a.getInt();
        debugPrint(x); //5
        System.println();
        x = (10/2);
        debugPrint(x); //5
        System.println();
        x = (4+a.getInt());
        debugPrint(x); //9
        System.println();
        x = (4+a.getInt())*6;
        debugPrint(x); //54
        System.println();
        x = (4+a.getInt())*6 - (10/2); //como no hay precedencia de operadores, no computa bien sin paréntesis
        debugPrint(x); //49
    }
}