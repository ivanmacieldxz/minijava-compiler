///[Error:finalMethod|7]
class Parent {
    final int finalMethod() { return 0; }
}

class Child extends Parent {
    int finalMethod() { return 1; }
}
