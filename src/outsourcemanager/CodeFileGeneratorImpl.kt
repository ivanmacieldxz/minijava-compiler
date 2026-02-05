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

}