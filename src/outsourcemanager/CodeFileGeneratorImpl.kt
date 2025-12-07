package outsourcemanager

import java.io.File

class CodeFileGeneratorImpl: CodeFileGenerator {

    lateinit var file: File

    override fun createFile(fileName: String) {
        file = File(fileName)
        println(file.absolutePath)
        file.writeText("")
    }

    override fun write(str: String) {
        try {
            file.appendText("$str\n")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun writeCodeSectionHeader() {
        write(".CODE")
    }

    override fun writeDataSectionHeader() {
        write(".DATA")
    }

    override fun writeHeapSectionHeader() {
        write(".HEAP")
    }

    override fun writeStackSectionHeader() {
        write(".STACK")
    }

    override fun writePush(str: String) {
        write("PUSH $str")
    }

    override fun writeCall() {
        write("CALL")
    }

    override fun writeHalt() {
        write("HALT")
    }

    override fun writeAuxRoutines() {
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

    override fun writeLabeledInstruction(label: String, inst: String) {
        write("$label: $inst")
    }

    override fun writeFreeLocalVars(num: Int) {
        write("FMEM $num")
    }

    override fun writeStore(offset: Int) {
        write("STORE $offset")
    }

    override fun writeLoad(offset: Int) {
        write("LOAD $offset")
    }

    override fun writeStoreFP(offset: Int) {
        write("STOREFP $offset")
    }

    override fun writeLoadFP(offset: Int) {
        write("LOADFP $offset")
    }

    override fun writeStoreSP(offset: Int) {
        write("STORESP $offset")
    }

    override fun writeLoadSP(offset: Int) {
        write("LOADSP $offset")
    }

    override fun writeRet(num: Int) {
        write("RET $num")
    }

    override fun writeLoadRef(offset: Int) {
        write("LOADREF $offset")
    }

    override fun writeStoreRef(offset: Int) {
        write("STOREREF $offset")
    }

    override fun writeRMEM(locations: Int) {
        write("RMEM $locations")
    }

    override fun writeDW(label: String, string: String) {
        write("$label: $string, 0")
    }

}