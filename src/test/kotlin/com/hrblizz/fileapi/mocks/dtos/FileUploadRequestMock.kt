package mocks.dtos

import com.hrblizz.fileapi.data.dtos.FileUploadRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object FileUploadRequestMock {
    fun create(
        name: String = "MockFile.pdf",
        contentType: String = "application/pdf",
        source: String = "mock source",
        expireTime: LocalDateTime? = LocalDateTime.parse("2027-01-01T00:00:00", DateTimeFormatter.ISO_DATE_TIME),
        meta: String = "{\"creatorEmployeeId\": 1}"
    ): FileUploadRequest {
        return FileUploadRequest(name, contentType, source, expireTime, meta)
    }
}
