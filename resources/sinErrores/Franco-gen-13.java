///2&0&true&false&true&false&true&true&false&true&false&false&true&exitosamente


class Init{
    static void main() {
        debugPrint(2 * 6 / 3 + 2); // 6
        debugPrint(1 % 3 - 2 ); // -1
        System.printBln(true && false || true); // true
        System.printBln(0 > 1); // false
        System.printBln(1 >= 1); // true
        System.printBln(1 < 0); // false
        System.printBln(1 <= 1); // true
        System.printBln(1 == 1); // true
        System.printBln(1 == 0); // false
        System.printBln(1 != 0); // true
        System.printBln(1 != 1); // false
        System.printBln(!true); // false
        System.printBln(!false); // true
    }
}
