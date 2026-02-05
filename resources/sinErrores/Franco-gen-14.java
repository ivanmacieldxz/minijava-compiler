///1&exitosamente


class Init {
    int x;
    int y;

    static void main() {
        new Init().test();
    }

    void test() {
        x = 1;
        this.y = this.x;
        debugPrint(y);
    }
}
