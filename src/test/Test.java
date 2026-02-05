package test;

import java.lang.reflect.Array;

class Test {
    String x = "en Test";
}
class Test2 extends Test {
    String x = "en Test 2";

    Test2() {
        super();
        //super(); no se puede dos veces
    }
}

class Test3 extends Test2 {
    public void testX() {
        System.out.println("Accediendo como x: " + x);
        System.out.println("Accediendo como super.x: " + super.x);
    }
}

class Main {
    public static void main(String[] args) {
        Test3 x = new Test3();

        x.testX();
    }

    void x(Main2 m) {
        Main2.main(new String[0]);
    }
}

class Main2 extends Main {

}