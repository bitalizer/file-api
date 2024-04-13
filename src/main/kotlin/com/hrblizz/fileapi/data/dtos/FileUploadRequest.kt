package com.hrblizz.fileapi.data.dtos

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime


data class FileUploadRequest(
    val name: String,
    val contentType: String,
    val source: String,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val expireTime: LocalDateTime?,
    val content: MultipartFile,
    val meta: String
)
