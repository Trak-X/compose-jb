package org.jetbrains.compose.desktop.application.internal.files

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.util.*
import java.util.zip.ZipInputStream

internal fun jarHash(file: File): String {
    val md5 = MessageDigest.getInstance("MD5")
    val entryMap = TreeMap<String, ByteArray>()
    ZipInputStream(FileInputStream(file).buffered()).use { zin ->
        for (entry in generateSequence { zin.nextEntry }) {
            if (!entry.isDirectory) {
                entryMap[entry.name] = zin.readAllBytes()
            }
        }
    }
    entryMap.forEach { k, v ->
        md5.update(k.toByteArray())
        md5.update(v)
    }
    val digest = md5.digest()
    return buildString(digest.size * 2) {
        for (byte in digest) {
            append(Integer.toHexString(0xFF and byte.toInt()))
        }
    }
}