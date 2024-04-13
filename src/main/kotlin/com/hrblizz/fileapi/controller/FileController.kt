package com.hrblizz.fileapi.controller

import com.hrblizz.fileapi.data.dtos.*
import com.hrblizz.fileapi.service.FileService
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/files")
class FileController(private val fileService: FileService) {

    @PostMapping(consumes = [MULTIPART_FORM_DATA_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun uploadFile(
        fileUploadRequest: FileUploadRequest
    ): ResponseEntity<FileUploadResponse> {

        val fileUploadResponse = fileService.uploadFile(fileUploadRequest)
        return ResponseEntity.ok(fileUploadResponse)
    }

    @PostMapping("/metas", consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
    fun getFileMetaDataBatch(@RequestBody fileMetaDataBatchRequest: FileMetaDataBatchRequest): ResponseEntity<FileMetaDataBatchResponse> {
        val fileMetaDataBatchResponse = fileService.getFileMetaDataBatch(fileMetaDataBatchRequest)
        return ResponseEntity.ok(fileMetaDataBatchResponse)
    }

    @GetMapping("/{token}")
    fun downloadFileByToken(@PathVariable token: String): ResponseEntity<InputStreamResource> {
        val downloadFileResponse = fileService.downloadFileByToken(token)

        val inputStreamResource = InputStreamResource(downloadFileResponse.stream)
        val headers = HttpHeaders()
        headers.set("X-Filename", downloadFileResponse.name)
        headers.set("X-Filesize", downloadFileResponse.size.toString())
        headers.set("X-CreateTime", downloadFileResponse.createTime.toString())
        headers.set("Content-Disposition", "attachment; filename=\"${downloadFileResponse.name}\"")
        headers.set(HttpHeaders.CONTENT_TYPE, downloadFileResponse.contentType)

        return ResponseEntity.ok().headers(headers).body(inputStreamResource)
    }

    @DeleteMapping("/{token}")
    fun deleteFileByToken(@PathVariable token: String): ResponseEntity<Void> {
        fileService.deleteFileByToken(token)
        return ResponseEntity.noContent().build()
    }
}
