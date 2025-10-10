///[Error:overriddenMethod|7]
class Parent {
    int overriddenMethod(int a, int b) { return 0; }
}

class Child extends Parent {
    int overriddenMethod(int a) { return a; }
}
