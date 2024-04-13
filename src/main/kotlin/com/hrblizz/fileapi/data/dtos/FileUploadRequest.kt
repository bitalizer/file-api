package com.hrblizz.fileapi.data.dtos

import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime


data class FileUploadRequest(
    val name: String,
    val contentType: String,
    val source: String,
    val expireTime: LocalDateTime?,
    val content: MultipartFile,
    val meta: String
)
