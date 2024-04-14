package mocks.entities

import com.hrblizz.fileapi.data.entities.FileMetadata
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object FileMetadataMock {
    fun create(
        token: String = "49cdcd82-4cf9-4c72-9d7c-88f184830c9a",
        name: String = "MockFile.pdf",
        size: Long = 1024L,
        contentType: String = "application/pdf",
        createTime: LocalDateTime = LocalDateTime.parse("2024-01-01T00:00:00", DateTimeFormatter.ISO_DATE_TIME),
        expireTime: LocalDateTime? = LocalDateTime.parse("2027-01-01T00:00:00", DateTimeFormatter.ISO_DATE_TIME),
        source: String = "mock source",
        meta: Map<String, Any> = mapOf("creatorEmployeeId" to 1)
    ): FileMetadata {
        return FileMetadata(token, name, size, contentType, createTime, expireTime, source, meta)
    }

    fun createList(tokens: List<String>): List<FileMetadata> {
        return tokens.mapIndexed { index, token -> create(token = token, name = "MockFile${index + 1}.pdf") }
    }
}
