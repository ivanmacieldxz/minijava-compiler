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
    fun writeStore(offset: Int)
    fun writeStoreFP(offset: Int)
    fun writeLoad(offset: Int)
    fun writeLoadFP(offset: Int)
    fun writeRet(num: Int)
    fun writeLoadSP(offset: Int)
    fun writeStoreSP(offset: Int)
    fun writeLoadRef(offset: Int)
    fun writeStoreRef(offset: Int)
    fun writeRMEM(locations: Int)
    fun writeDW(label: String, string: String)
}