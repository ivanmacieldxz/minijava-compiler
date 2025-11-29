class Test {
    static void m1(Test p1) {
        p1.m3();
    }
    static void m3() {

    }
}
class Test2 extends Test {
    void m2(Test p1) {
        p1.m3();
    }
}
//class Test3 {
//    void x(Test p1) {
//        p1.m3();
//    }
//}