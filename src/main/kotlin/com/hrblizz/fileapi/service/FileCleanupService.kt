package com.hrblizz.fileapi.service

import com.hrblizz.fileapi.data.repository.FileMetadataRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * Service responsible for cleaning up expired files.
 *
 * @property fileMetadataRepository The repository for file metadata.
 * @property fileStorageService The service for file storage operations.
 */
@Service
class FileCleanupService(
    private val fileMetadataRepository: FileMetadataRepository,
    private val fileStorageService: FileStorageService,
) {
    /**
     * Scheduled task to remove expired files.
     * This method is executed at a fixed rate of once per minute.
     * It retrieves expired files from the [fileMetadataRepository] and deletes them from both the database and the file storage.
     */
    @Scheduled(fixedRate = 60_000, initialDelay = 0)
    fun removeExpiredFiles() {
        val expiredFiles = fileMetadataRepository.findByExpireTimeBefore(LocalDateTime.now())

        expiredFiles.forEach { fileMetadata ->
            fileMetadataRepository.delete(fileMetadata)
            fileStorageService.delete(fileMetadata.token)
        }
    }
}
