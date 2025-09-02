package sourcemanager

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class SourceManagerEfImpl: SourceManager {

    private var currentChar: Char? = null
    private lateinit var bufferedReader: BufferedReader
    private var lineNumber = 1
    private var incrementLineNumberOnNextRead = false

    override fun open(filePath: String?) {
        val fileInputStream = FileInputStream(filePath!!)
        bufferedReader = InputStreamReader(fileInputStream, StandardCharsets.UTF_8).buffered()
    }

    override fun close() {
        bufferedReader.close()
    }

    override fun getNextChar(): Char {
        currentChar = bufferedReader.readNextChar()

        if (incrementLineNumberOnNextRead) {
            lineNumber++
        }

        if (currentChar == '\r') {
            currentChar = bufferedReader.readNextChar()
        }

        incrementLineNumberOnNextRead = currentChar == '\n'

        return currentChar!!
    }

    override fun getLineNumber() = lineNumber

    @Suppress("NOTHING_TO_INLINE")
    private inline fun BufferedReader.readNextChar(): Char =
        read().takeIf { it != -1 }?.toChar() ?: SourceManager.END_OF_FILE
}