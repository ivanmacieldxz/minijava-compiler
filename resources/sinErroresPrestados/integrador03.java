///[SinErrores]
class C {
    void test() {
        this.print().show().done();
        //this.print().show(10).done();
    }

    C print() { return this; }
    C show(int x) { return this; }
    C done() { return this; }
}
