package ru.spbsu.kotlin

import java.io.File
import java.io.OutputStream

class EncodeStream (private val rootFolder: File, private val password: String): OutputStream() {
    override fun write(b: Int) {
        TODO()
    }
}