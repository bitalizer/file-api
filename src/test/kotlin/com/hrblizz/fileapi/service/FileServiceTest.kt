package com.hrblizz.fileapi.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.hrblizz.fileapi.controller.exception.BadRequestException
import com.hrblizz.fileapi.controller.exception.NotFoundException
import com.hrblizz.fileapi.data.dtos.FileMetaDataBatchRequest
import com.hrblizz.fileapi.data.entities.FileMetadata
import com.hrblizz.fileapi.data.repository.FileMetadataRepository
import io.mockk.*
import mocks.dtos.FileUploadRequestMock
import mocks.entities.FileMetadataMock
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.mock.web.MockMultipartFile
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class FileServiceTest {

    private val fileMetadataRepository: FileMetadataRepository = mockk()
    private val fileStorageService: FileStorageService = mockk()
    private val objectMapper: ObjectMapper = spyk(ObjectMapper())
    private val fileService = FileService(fileMetadataRepository, fileStorageService, objectMapper)

    @Test
    fun uploadFile_Success() {

        val fileUploadRequest = FileUploadRequestMock.create()

        val mockMultipartFile = MockMultipartFile(
            fileUploadRequest.name, fileUploadRequest.name, fileUploadRequest.contentType, byteArrayOf(1, 2, 3)
        )

        val expectedMetaMap = mapOf("creatorEmployeeId" to 1)
        val savedStream = slot<InputStream>()

        every { fileMetadataRepository.save(any<FileMetadata>()) } answers { arg<FileMetadata>(0) }
        justRun { fileStorageService.save(any<InputStream>(), any<String>()) }

        val fileUploadResponse = fileService.uploadFile(fileUploadRequest, mockMultipartFile)
        val savedFileMetadata = slot<FileMetadata>()

        verify(exactly = 1) { objectMapper.readValue(fileUploadRequest.meta, any<TypeReference<Map<String, Any>>>()) }
        verify(exactly = 1) { fileMetadataRepository.save(capture(savedFileMetadata)) }
        verify(exactly = 1) { fileStorageService.save(capture(savedStream), fileUploadResponse.token) }

        assertEquals(savedFileMetadata.captured.name, fileUploadRequest.name)
        assertEquals(savedFileMetadata.captured.contentType, fileUploadRequest.contentType)
        assertEquals(savedFileMetadata.captured.token, fileUploadResponse.token)
        assertEquals(savedFileMetadata.captured.meta, expectedMetaMap)
        assertArrayEquals(mockMultipartFile.bytes, savedStream.captured.readBytes())
    }

    @Test
    fun uploadFile_invalidMetaJson() {
        val fileUploadRequest = FileUploadRequestMock.create(meta = "invalid json")

        val mockMultipartFile = MockMultipartFile(
            fileUploadRequest.name, fileUploadRequest.name, fileUploadRequest.contentType, byteArrayOf(1, 2, 3)
        )

        assertThrows<BadRequestException> {
            fileService.uploadFile(fileUploadRequest, mockMultipartFile)
        }
    }

    @Test
    fun getFileMetaDataBatch_Success() {

        val tokenList = listOf("token1", "token2")
        val fileMetadataList = FileMetadataMock.createList(tokenList)


        every { fileMetadataRepository.findAllById(tokenList) } returns fileMetadataList

        val fileMetaDataBatchRequest = FileMetaDataBatchRequest(tokenList)
        val fileMetaDataBatchResponse = fileService.getFileMetaDataBatch(fileMetaDataBatchRequest)

        verify(exactly = 1) { fileMetadataRepository.findAllById(tokenList) }

        assertEquals(fileMetadataList.size, fileMetaDataBatchResponse.files.size)
        assertEquals(fileMetadataList[0].name, fileMetaDataBatchResponse.files["token1"]?.filename)
        assertEquals(fileMetadataList[1].name, fileMetaDataBatchResponse.files["token2"]?.filename)
    }

    @Test
    fun getFileMetaDataBatch_TokenNotFound_ReturnsEmptyMap() {
        val nonExistentToken = "non-existent-token"
        val fileMetaDataBatchRequest = FileMetaDataBatchRequest(listOf(nonExistentToken))

        every { fileMetadataRepository.findAllById(fileMetaDataBatchRequest.tokens) } returns emptyList()

        val fileMetaDataBatchResponse = fileService.getFileMetaDataBatch(fileMetaDataBatchRequest)

        verify(exactly = 1) { fileMetadataRepository.findAllById(fileMetaDataBatchRequest.tokens) }

        assertTrue(fileMetaDataBatchResponse.files.isEmpty())
    }

    @Test
    fun getFileMetaDataBatch_ExcludeExpiredTokens() {

        val validToken = "valid-token"
        val expiredToken = "expired-token"

        val fileMetadataList = mutableListOf<FileMetadata>()

        fileMetadataList.add(FileMetadataMock.create(token = validToken))
        fileMetadataList.add(
            FileMetadataMock.create(
                token = expiredToken,
                expireTime = LocalDateTime.parse("2022-01-01T00:00:00", DateTimeFormatter.ISO_DATE_TIME)
            )
        )

        val tokens = fileMetadataList.map { it.token }

        every { fileMetadataRepository.findAllById(tokens) } returns fileMetadataList

        val fileMetaDataBatchRequest = FileMetaDataBatchRequest(tokens)
        val fileMetaDataBatchResponse = fileService.getFileMetaDataBatch(fileMetaDataBatchRequest)

        verify(exactly = 1) { fileMetadataRepository.findAllById(tokens) }

        assertTrue(fileMetaDataBatchResponse.files.containsKey(validToken))
        assertTrue(!fileMetaDataBatchResponse.files.containsKey(expiredToken))
    }

    @Test
    fun downloadFileByToken_Success() {
        val validToken = "valid-token"
        val expectedContent = byteArrayOf(1, 2, 3)
        val fileMetadata = FileMetadataMock.create(token = validToken)

        every { fileMetadataRepository.findById(validToken) } returns Optional.of(fileMetadata)
        every { fileStorageService.load(validToken) } returns expectedContent.inputStream()

        val downloadFileResponse = fileService.downloadFileByToken(validToken)

        verify(exactly = 1) { fileMetadataRepository.findById(validToken) }
        verify(exactly = 1) { fileStorageService.load(validToken) }

        assertEquals(fileMetadata.name, downloadFileResponse.name)
        assertArrayEquals(expectedContent, downloadFileResponse.stream.readBytes())
    }

    @Test
    fun downloadFileByToken_FileNotFound() {
        val nonExistentToken = "non-existent-token"

        every { fileMetadataRepository.findById(nonExistentToken) } returns Optional.empty()

        assertThrows<NotFoundException> {
            fileService.downloadFileByToken(nonExistentToken)
        }
    }

    @Test
    fun deleteFileByToken_Success() {

        val validToken = "valid-token"
        val fileMetadata = FileMetadataMock.create(token = validToken)

        every { fileMetadataRepository.findById(validToken) } returns Optional.of(fileMetadata)
        justRun { fileMetadataRepository.deleteById(validToken) }
        justRun { fileStorageService.delete(validToken) }

        fileService.deleteFileByToken(validToken)

        verify(exactly = 1) { fileMetadataRepository.deleteById(validToken) }
        verify(exactly = 1) { fileStorageService.delete(validToken) }
    }

    @Test
    fun deleteFileByToken_TokenNotFound() {
        val nonExistentToken = "non-existent-token"

        every { fileMetadataRepository.findById(nonExistentToken) } returns Optional.empty()

        assertThrows<NotFoundException> {
            fileService.deleteFileByToken(nonExistentToken)
        }

        verify(exactly = 0) { fileMetadataRepository.deleteById(nonExistentToken) }
        verify(exactly = 0) { fileStorageService.delete(nonExistentToken) }
    }
}
