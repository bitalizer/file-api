package com.hrblizz.fileapi.service

import java.io.InputStream

interface FileStorageService {
    fun init()
    fun save(inputStream: InputStream, fileName: String)
    fun load(filename: String): InputStream
    fun delete(filename: String)
}
