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

    static class Y {

        public static void main(String[] args) {
            int x = 2; //puedo hacer shadow de atributos :)

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
