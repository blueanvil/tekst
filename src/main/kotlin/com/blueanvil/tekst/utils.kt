package com.blueanvil.tekst

import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets

private const val BUFFER_SIZE = 64 * 1024

/**
 * @author Cosmin Marginean
 */
fun InputStream.writeTo(output: OutputStream): Long {
    val buffer = ByteArray(BUFFER_SIZE)
    var count: Long = 0
    while (true) {
        val n = read(buffer)
        if (n == -1) break
        output.write(buffer, 0, n)
        count += n.toLong()
    }
    return count
}

fun InputStream.asString(): String {
    val output = ByteArrayOutputStream(BUFFER_SIZE)
    writeTo(output)
    return String(output.toByteArray(), StandardCharsets.UTF_8)
}