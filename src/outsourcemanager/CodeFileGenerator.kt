package outsourcemanager

interface CodeFileGenerator {

    fun createFile(fileName: String)
    fun write(str: String)

    fun writeCodeSectionHeader() {
        write(".CODE")
    }

    fun writeDataSectionHeader() {
        write(".DATA")
    }

    fun writeHeapSectionHeader() {
        write(".HEAP")
    }

    fun writeStackSectionHeader() {
        write(".STACK")
    }

    fun writePush(str: String) {
        write("PUSH $str")
    }

    fun writeCall() {
        write("CALL")
    }

    fun writeHalt() {
        write("HALT")
    }

    fun writeAuxRoutines() {
        write(
            "\nsimple_heap_init: RET 0\n" +
                    "simple_malloc: LOADFP\n" +
                    "LOADSP\n" +
                    "STOREFP\n" +
                    "LOADHL\n" +
                    "DUP\n" +
                    "PUSH 1\n" +
                    "ADD\n" +
                    "STORE 4\n" +
                    "LOAD 3\n" +
                    "ADD\n" +
                    "STOREHL\n" +
                    "STOREFP\n" +
                    "RET 1\n"
        )
    }

    fun writeLabeledInstruction(label: String, inst: String) {
        write("$label: $inst")
    }

    fun writeFreeLocalVars(num: Int) {
        write("FMEM $num")
    }

    fun writeStore(offset: Int) {
        write("STORE $offset")
    }

    fun writeLoad(offset: Int) {
        write("LOAD $offset")
    }

    fun writeStoreFP(offset: Int) {
        write("STOREFP $offset")
    }

    fun writeLoadFP(offset: Int) {
        write("LOADFP $offset")
    }

    fun writeStoreSP(offset: Int) {
        write("STORESP $offset")
    }

    fun writeLoadSP(offset: Int) {
        write("LOADSP $offset")
    }

    fun writeRet(num: Int) {
        write("RET $num")
    }

    fun writeLoadRef(offset: Int) {
        write("LOADREF $offset")
    }

    fun writeStoreRef(offset: Int) {
        write("STOREREF $offset")
    }

    fun writeRMEM(locations: Int) {
        write("RMEM $locations")
    }

    fun writeDW(label: String, string: String) {
        write("$label: $string, 0")
    }

    fun writeSwap() {
        write("SWAP")
    }

    fun writeDup() {
        write("DUP")
    }
}