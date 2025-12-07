package test;

class Test {
    String x = "en Test";
}
class Test2 extends Test {
    String x = "en Test 2";
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
}