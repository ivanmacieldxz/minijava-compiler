///[SinErrores]
abstract class Calc {
    int acc;

    void reset() {
        acc = 0;
    }

    int sum(int a, int b) {
        var result=0; // hay que ver si se puede poner var int result  o sin poner la asignacion
        result = a + b;
        return result;
    }
}
