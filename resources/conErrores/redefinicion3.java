///[Error:overriddenMethod|7]
class Parent {
    int overriddenMethod(int a) { return 1; }
}

class Child extends Parent {
    void overriddenMethod(int a) { }
}
