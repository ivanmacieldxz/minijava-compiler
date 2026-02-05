///6&5&2&4&6&8&9&10&exitosamente
class TestIf {
    static void main() {
        var x = 5;
        System.printIln(++x);
        System.printIln(--x);

        var y = 5;

        if (x > y) {
            System.printIln(1);
        } else {
            System.printIln(2);
        }

        if (x < y) {
            System.printIln(3);
        } else {
            System.printIln(4);
        }

        if (x > y) {
            System.printIln(5);
        }
        System.printIln(6);

        if (x < y) {
            System.printIln(7);
        }
        System.printIln(8);

        if (x == y){
            System.printIln(9);
        }

        if((x + 1) == (y + 1)){
            System.printIln(10);
        }else{
            System.printIln(11);
        }
    }
}