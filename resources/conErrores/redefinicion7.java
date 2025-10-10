///[Error:foo|7]
class Parent {
    int foo() { return 0; }
}

class Child extends Parent {
    static int foo() { return 1; }
}
