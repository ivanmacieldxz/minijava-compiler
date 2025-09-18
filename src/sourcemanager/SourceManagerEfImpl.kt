package sourcemanager

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class SourceManagerEfImpl: SourceManager {

    private var currentChar: Char? = null
    private lateinit var bufferedReader: BufferedReader
    private var lineNumber = 1
    private var wentPastNewLine = false
    private var columnNumber = 0

    override fun open(filePath: String?) {
        val fileInputStream = FileInputStream(filePath!!)
        bufferedReader = InputStreamReader(fileInputStream, StandardCharsets.UTF_8).buffered()
        columnNumber = 0
    }

    override fun close() {
        bufferedReader.close()
    }

    override fun getNextChar(): Char {
        currentChar = bufferedReader.readNextChar()
        columnNumber++

        if (wentPastNewLine) {
            lineNumber++
            columnNumber = 1
        }

        if (currentChar == '\r') {
            currentChar = bufferedReader.readNextChar()
        }

        wentPastNewLine = currentChar == '\n'

        return currentChar!!
    }

    override fun getLineNumber() = lineNumber
    override fun getColumnNumber() = columnNumber

    @Suppress("NOTHING_TO_INLINE")
    private inline fun BufferedReader.readNextChar(): Char =
        read().takeIf { it != -1 }?.toChar() ?: SourceManager.END_OF_FILE
}