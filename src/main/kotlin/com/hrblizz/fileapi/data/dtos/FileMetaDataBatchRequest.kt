package com.hrblizz.fileapi.data.dtos

import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

data class FileMetaDataBatchRequest(
    @field:NotNull
    @field:Size(min = 1, max = 100)
    val tokens: List<String> = emptyList()
)
