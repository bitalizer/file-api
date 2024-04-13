package com.hrblizz.fileapi.data.dtos

data class FileMetaDataMapResponse(
    val files: Map<String, FileMetaDataResponse>
)
