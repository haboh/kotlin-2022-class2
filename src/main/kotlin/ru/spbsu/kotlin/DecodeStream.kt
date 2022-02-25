package ru.spbsu.kotlin

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.experimental.xor

class DecodeStream(rootFolder: File) : InputStream() {
    private var posInMessage = 0
    private var password: List<Int> = arrayListOf()
    private var message: ByteArray = byteArrayOf()

    init {
        val order = generateCorrectFileOrder(rootFolder)
        readFilesInCorrectOrder(order)
        password = password.takeWhile { it != 0 }
    }

    private fun rearrangeFolder(folder: File): List<File> {
        return folder.walk()
            .toList()
            .filter { it.absolutePath.count { it == '/' } == folder.absolutePath.count { it == '/' } + 1 }
            .sortedBy { it.name }
    }

    private fun generateCorrectFileOrder(folder: File): List<File> {
        if (!folder.exists() || !folder.isDirectory) {
            throw IllegalArgumentException("Error with folder $folder")
        }
        val (directories, files) = rearrangeFolder(folder).partition { it.isDirectory }
        return directories.flatMap { directory -> generateCorrectFileOrder(directory) } + files
    }

    private fun readFilesInCorrectOrder(order: List<File>) {
        order.forEach { file ->
            password += Date(file.lastModified()).seconds % 10
            message += file.readBytes()
        }
    }

    override fun read(): Int {
        if (posInMessage == message.size) {
            return -1
        }
        if (password.isEmpty()) {
            throw IllegalArgumentException("Password is empty")
        }
        val curPasswordPosition = posInMessage % password.size
        val curPasswordByte = password[curPasswordPosition].toByte()
        val curMessageByte = message[posInMessage]
        val curByte = curPasswordByte.xor(curMessageByte)
        posInMessage++
        return curByte.toInt()
    }
}