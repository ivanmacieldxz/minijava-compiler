package test;

public class Test {

    int x;

    static class X {
        public static void main(String[] args) {

        }
        static void y() {
//            x();
        }

        public void x() {
            y();
        }
        public X() {

            y();
            x();
        }
    }

    static class Y extends X {

        static void y() {

        }

        public static void main(String[] args) {
            int x = 2; //puedo hacer shadow de atributos :)

            var a = new X();
            var b = new X();

//            var args = 1;

//            var c = a + b;

//            if (1 + new Y()) {
//
//            }

        }

        public void example(int x, int y) {
            main(new String[1]);
        }

        public void callsExample() {
//            example(1, 2, 3);
//
//            double v = 1;
//            v.a;

//            return 1;
        }
    }

}
