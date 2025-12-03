package outsourcemanager

interface CodeFileGenerator {

    fun createFile(fileName: String)
    fun write(str: String)
    fun writeCodeSectionHeader()
    fun writeDataSectionHeader()
    fun writeHeapSectionHeader()
    fun writeStackSectionHeader()
    fun writePush(str: String)
    fun writeCall()
    fun writeHalt()
    fun writeAuxRoutines()

    fun writeLabeledInstruction(label: String, inst: String)
    fun writeFreeLocalVars(num: Int)
    fun writeStore(num: Int)
    fun writeStoreFP(num: Int)
    fun writeLoad(num: Int)
    fun writeLoadFP(num: Int)
    fun writeRet(num: Int)
    fun writeLoadSP(num: Int)
    fun writeStoreSP(num: Int)
}