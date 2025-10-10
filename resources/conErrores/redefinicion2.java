///[Error:int|7]
class Parent {
    int overriddenMethod(int a, String s) { return 0; }
}

class Child extends Parent {
    int overriddenMethod(int a, int s) { return 0; }
}
