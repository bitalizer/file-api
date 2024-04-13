package com.hrblizz.fileapi.data.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "files")
data class FileMetadata(
    @Id val token: String,
    val name: String,
    val size: Long,
    val contentType: String,
    val createTime: LocalDateTime,
    val expireTime: LocalDateTime?,
    val source: String,
    val meta: Map<String, Any>
)
