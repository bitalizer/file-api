package com.hrblizz.fileapi.util

import java.io.InputStream
import java.io.OutputStream
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterInputStream

object CompressionUtil {

    fun compress(input: InputStream, output: OutputStream) {
        DeflaterOutputStream(output, Deflater()).use { dos ->
            input.copyTo(dos)
        }
    }

    fun decompress(input: InputStream): InputStream {
        return InflaterInputStream(input)
    }
}
