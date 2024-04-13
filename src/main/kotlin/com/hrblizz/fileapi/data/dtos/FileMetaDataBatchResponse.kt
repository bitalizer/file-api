package com.hrblizz.fileapi.data.dtos

data class FileMetaDataBatchResponse(
    val files: Map<String, FileMetaDataResponse>
)
