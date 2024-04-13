package com.hrblizz.fileapi.data.dtos

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import javax.validation.constraints.Future
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size


class FileUploadRequest(
    @field:NotBlank
    @field:Size(min = 4, max = 255)
    val name: String = "",
    @field:NotBlank
    @field:Size(min = 1, max = 255)
    val contentType: String = "",
    @field:NotBlank
    @field:Size(min = 3, max = 255)
    val source: String = "",
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @field:Future
    val expireTime: LocalDateTime?,
    @field:NotBlank
    val meta: String = ""
)
