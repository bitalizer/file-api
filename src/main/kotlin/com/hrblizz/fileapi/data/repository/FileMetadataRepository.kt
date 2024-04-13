package com.hrblizz.fileapi.data.repository

import com.hrblizz.fileapi.data.entities.FileMetadata
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface FileMetadataRepository : MongoRepository<FileMetadata, String> {
    fun findByExpireTimeBefore(expireTime: LocalDateTime): List<FileMetadata>
}
