///[Error:+|6]
class X {
    int v1;

    void m1(int p1) {
        if (p1 + v1)
            p1 = 10;
    }
}