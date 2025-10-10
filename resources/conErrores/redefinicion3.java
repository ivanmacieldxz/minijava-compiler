///[Error:overriddenMethod|7]
class Parent {
    int overriddenMethod() { return 1; }
}

class Child extends Parent {
    void overriddenMethod() { }
}
