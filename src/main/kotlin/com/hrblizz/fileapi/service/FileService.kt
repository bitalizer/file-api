package com.hrblizz.fileapi.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.hrblizz.fileapi.controller.exception.BadRequestException
import com.hrblizz.fileapi.controller.exception.NotFoundException
import com.hrblizz.fileapi.data.dtos.*
import com.hrblizz.fileapi.data.entities.FileMetadata
import com.hrblizz.fileapi.data.repository.FileMetadataRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class FileService(
    private val fileMetadataRepository: FileMetadataRepository,
    private val fileStorageService: FileStorageService,
    private val objectMapper: ObjectMapper
) {

    fun uploadFile(fileUploadRequest: FileUploadRequest): FileUploadResponse {
        val metaMap = parseJsonStringAsMap(fileUploadRequest.meta)

        val fileMetadata = FileMetadata(
            UUID.randomUUID().toString(),
            fileUploadRequest.name,
            fileUploadRequest.content.size,
            fileUploadRequest.contentType,
            LocalDateTime.now(),
            fileUploadRequest.expireTime,
            fileUploadRequest.source,
            metaMap
        )

        val savedFileInfo = fileMetadataRepository.save(fileMetadata)
        fileStorageService.save(fileUploadRequest.content.inputStream, savedFileInfo.token)

        return FileUploadResponse(savedFileInfo.token)
    }

    fun getFileMetaDataBatch(fileMetaDataBatchRequest: FileMetaDataBatchRequest): FileMetaDataBatchResponse {
        val metadataList = fileMetadataRepository.findAllById(fileMetaDataBatchRequest.tokens)
        val metadataMap = metadataList.associate { it.token to mapToFileResponse(it) }
        return FileMetaDataBatchResponse(metadataMap)
    }

    fun downloadFileByToken(token: String): DownloadFileResponse {
        val fileMetadata = getFileMetadataByToken(token)
        val stream = fileStorageService.load(fileMetadata.token)
        return DownloadFileResponse(
            fileMetadata.name,
            fileMetadata.size,
            fileMetadata.contentType,
            fileMetadata.createTime,
            stream
        )
    }

    fun deleteFileByToken(token: String) {
        val fileMetadata = getFileMetadataByToken(token)
        fileMetadataRepository.deleteById(fileMetadata.token)
        fileStorageService.delete(fileMetadata.token)
    }

    private fun getFileMetadataByToken(token: String): FileMetadata {
        return fileMetadataRepository.findById(token)
            .orElseThrow { NotFoundException("File with token $token not found") }
    }

    private fun mapToFileResponse(fileMetadata: FileMetadata): FileMetaDataResponse {
        return FileMetaDataResponse(
            fileMetadata.token,
            fileMetadata.name,
            fileMetadata.size,
            fileMetadata.contentType,
            fileMetadata.createTime,
            fileMetadata.meta
        )
    }

    private fun parseJsonStringAsMap(json: String): Map<String, Any> {
        return try {
            objectMapper.readValue(json)
        } catch (e: JsonProcessingException) {
            throw BadRequestException("Invalid JSON: ${e.message}")
        }
    }
}
