package com.hrblizz.fileapi.data.dtos

import java.time.LocalDateTime

data class FileMetaDataResponse(
    val token: String,
    val filename: String,
    val size: Long,
    val contentType: String,
    val createTime: LocalDateTime,
    val meta: Map<String, Any>
)
