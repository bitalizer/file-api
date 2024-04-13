package com.hrblizz.fileapi.service

import com.hrblizz.fileapi.util.CompressionUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.annotation.PostConstruct

@Service
class LocalStorageService(
    @Value("\${storage.dir:uploads}")
    private val storageDir: String
) : FileStorageService {

    private val root: Path = Paths.get(storageDir)

    @PostConstruct
    override fun init() {
        try {
            Files.createDirectories(root)
        } catch (e: Exception) {
            throw RuntimeException("Could not initialize folder for upload!", e)
        }
    }

    override fun save(inputStream: InputStream, fileName: String) {
        try {
            inputStream.use { input ->
                val outputFile = root.resolve(fileName)
                Files.newOutputStream(outputFile).use { output ->
                    CompressionUtil.compress(input, output)
                }
            }
        } catch (e: Exception) {
            throw RuntimeException("Could not store the file. Error: ${e.message}", e)
        }
    }

    override fun load(filename: String): InputStream {
        try {
            val file = root.resolve(filename)
            val resource = UrlResource(file.toUri())
            if (resource.exists() || resource.isReadable) {
                return CompressionUtil.decompress(resource.inputStream)
            } else {
                throw RuntimeException("Could not read the file!")
            }
        } catch (e: MalformedURLException) {
            throw RuntimeException("Error: ${e.message}", e)
        }
    }

    override fun delete(filename: String) {
        try {
            val fileToDelete = root.resolve(filename)
            if (Files.exists(fileToDelete)) {
                Files.delete(fileToDelete)
            } else {
                throw FileNotFoundException("File not found: $filename")
            }
        } catch (e: IOException) {
            throw RuntimeException("Error deleting file: ${e.message}", e)
        }
    }
}
