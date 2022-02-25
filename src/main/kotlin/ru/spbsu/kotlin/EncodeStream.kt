package ru.spbsu.kotlin

import java.io.File
import java.io.OutputStream


class EncodeStream(private val rootFolder: File, private val password: String) : OutputStream() {
    private val millisecondsInOnSecond = 1000L

    init {
        if (!rootFolder.exists()) {
            throw IllegalArgumentException("${rootFolder.name} doesn't exists")
        }
        if (password.toIntOrNull() == null || password.contains("0") || password.isEmpty()) {
            throw IllegalArgumentException("Error password $password")
        }
    }

    private val data = mutableListOf<Byte>()
    private var passwordIndex = 0

    override fun write(b: Int) {
        val currentPasswordByte = password[passwordIndex].code - '0'.code
        data.add(b.xor(currentPasswordByte).toByte())
        passwordIndex = (passwordIndex + 1) % password.length
    }

    override fun close() {
        val files = password.indices
            .map { File(rootFolder, it.toString()) }
            .sortedBy { it.name }
        files[0].writeBytes(data.toByteArray())
        files.indices.forEach { i ->
            files[i].createNewFile()
            val passwordPos = i % password.length
            val passwordByte = password[passwordPos].code - '0'.code
            files[i].setLastModified(passwordByte * millisecondsInOnSecond)
        }
    }
}