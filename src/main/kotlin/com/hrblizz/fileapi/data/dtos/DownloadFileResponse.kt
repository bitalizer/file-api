package com.hrblizz.fileapi.data.dtos

import java.io.InputStream
import java.time.LocalDateTime


data class DownloadFileResponse(
    val name: String,
    val size: Long,
    val contentType: String,
    val createTime: LocalDateTime,
    val stream: InputStream
)
