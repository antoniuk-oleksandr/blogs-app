package com.example.blogs.app.api.post.service;

import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.fixture.FileFixtures;
import com.example.blogs.app.api.file.service.FileService;
import com.example.blogs.app.api.post.exception.FailedToRollbackS3FileException;
import com.example.blogs.app.storage.S3BucketService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionalFileUploaderTest {

    @Mock
    private FileService fileService;

    @Mock
    private S3BucketService s3BucketService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private TransactionalFileUploader transactionalFileUploader;

    @BeforeEach
    void setUp() {
        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    void tearDown() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void uploadWithTransactionRollback_shouldUploadFileAndRegisterSynchronization_whenCalled() {
        String path = "/uploads/test";
        FileEntity fileEntity = FileFixtures.file();
       
        when(fileService.upload(multipartFile, path)).thenReturn(fileEntity);

        FileEntity result = transactionalFileUploader.uploadWithTransactionRollback(multipartFile, path);

        assertThat(result).isEqualTo(fileEntity);
        verify(fileService).upload(multipartFile, path);
        assertThat(TransactionSynchronizationManager.getSynchronizations()).hasSize(1);
    }

    @Test
    void uploadWithTransactionRollback_shouldReturnUploadedFileEntity_whenCalled() {
        String path = "/uploads/documents";
        FileEntity fileEntity = FileFixtures.file();
       
        when(fileService.upload(any(MultipartFile.class), anyString())).thenReturn(fileEntity);

        FileEntity result = transactionalFileUploader.uploadWithTransactionRollback(multipartFile, path);

        assertThat(result)
                .isNotNull()
                .isEqualTo(fileEntity)
                .satisfies(file -> {
                    assertThat(file.getFilePath()).isEqualTo("filePath");
                    assertThat(file.getUuid()).isEqualTo("uuid");
                    assertThat(file.getFileExtension()).isEqualTo("fileExtension");
                });
    }

    @Test
    void uploadWithTransactionRollback_shouldNotDeleteFile_whenTransactionCommits() {
        String path = "/uploads/test";
        FileEntity fileEntity = FileFixtures.file();
      
        when(fileService.upload(any(MultipartFile.class), anyString())).thenReturn(fileEntity);

        transactionalFileUploader.uploadWithTransactionRollback(multipartFile, path);
        TransactionSynchronization synchronization = TransactionSynchronizationManager.getSynchronizations().getFirst();
        synchronization.afterCompletion(TransactionSynchronization.STATUS_COMMITTED);

        verify(fileService, never()).delete(fileEntity);
        verify(s3BucketService, never()).delete(any(), any(), any());
    }

    @Test
    void uploadWithTransactionRollback_shouldDeleteFileFromServiceAndS3_whenTransactionRollsBack() {
        String path = "/uploads/test";
        FileEntity fileEntity = FileFixtures.file();
        
        when(fileService.upload(any(MultipartFile.class), anyString())).thenReturn(fileEntity);

        transactionalFileUploader.uploadWithTransactionRollback(multipartFile, path);
        TransactionSynchronization synchronization = TransactionSynchronizationManager.getSynchronizations().getFirst();
        synchronization.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);

        verify(fileService).delete(fileEntity);
        verify(s3BucketService).delete(
                "filePath",
                "uuid",
                "fileExtension"
        );
    }

    @Test
    void uploadWithTransactionRollback_shouldDeleteFileFromServiceAndS3_whenTransactionStatusIsUnknown() {
        String path = "/uploads/test";
        FileEntity fileEntity = FileFixtures.file();
        
        when(fileService.upload(any(MultipartFile.class), anyString())).thenReturn(fileEntity);

        transactionalFileUploader.uploadWithTransactionRollback(multipartFile, path);
        TransactionSynchronization synchronization = TransactionSynchronizationManager.getSynchronizations().getFirst();
        synchronization.afterCompletion(TransactionSynchronization.STATUS_UNKNOWN);

        verify(fileService).delete(fileEntity);
        verify(s3BucketService).delete(
                "filePath",
                "uuid",
                "fileExtension"
        );
    }

    @Test
    void uploadWithTransactionRollback_shouldThrowFailedToRollbackS3FileException_whenFileServiceDeleteFailsOnRollback() {
        String path = "/uploads/test";
        FileEntity fileEntity = FileFixtures.file();
        
        when(fileService.upload(any(MultipartFile.class), anyString())).thenReturn(fileEntity);
        RuntimeException deleteException = new RuntimeException("Delete failed");
        doThrow(deleteException).when(fileService).delete(fileEntity);

        transactionalFileUploader.uploadWithTransactionRollback(multipartFile, path);
        TransactionSynchronization synchronization = TransactionSynchronizationManager.getSynchronizations().getFirst();

        assertThatThrownBy(() -> synchronization.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK))
                .isInstanceOf(FailedToRollbackS3FileException.class)
                .hasCause(deleteException);

        verify(fileService).delete(fileEntity);
        verify(s3BucketService, never()).delete(anyString(), anyString(), anyString());
    }

    @Test
    void uploadWithTransactionRollback_shouldThrowFailedToRollbackS3FileException_whenS3DeleteFailsOnRollback() {
        String path = "/uploads/test";
        FileEntity fileEntity = FileFixtures.file();
        
        when(fileService.upload(any(MultipartFile.class), anyString())).thenReturn(fileEntity);
        RuntimeException s3Exception = new RuntimeException("S3 delete failed");
        doThrow(s3Exception).when(s3BucketService).delete(anyString(), anyString(), anyString());

        transactionalFileUploader.uploadWithTransactionRollback(multipartFile, path);
        TransactionSynchronization synchronization = TransactionSynchronizationManager.getSynchronizations().getFirst();

        assertThatThrownBy(() -> synchronization.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK))
                .isInstanceOf(FailedToRollbackS3FileException.class)
                .hasCause(s3Exception);
        verify(fileService).delete(fileEntity);
        verify(s3BucketService).delete("filePath", "uuid", "fileExtension");
    }

    @Test
    void uploadWithTransactionRollback_shouldPassCorrectPathToFileService_whenProvidedWithCustomPath() {
        String customPath = "/custom/uploads/images";
        FileEntity fileEntity = FileFixtures.file();
        
        when(fileService.upload(any(MultipartFile.class), anyString())).thenReturn(fileEntity);

        transactionalFileUploader.uploadWithTransactionRollback(multipartFile, customPath);

        verify(fileService).upload(multipartFile, customPath);
    }

    @Test
    void uploadWithTransactionRollback_shouldDeleteWithCorrectFileEntityParameters_whenTransactionRollsBack() {
        String filePath = "/custom/path";
        String uuid = "custom-uuid";
        String fileExtension= ".png";
        FileEntity customFileEntity = FileEntity.builder()
                .filePath(filePath)
                .uuid(uuid)
                .fileExtension(fileExtension)
                .build();
        String path = "/uploads/test";

        when(fileService.upload(any(MultipartFile.class), anyString())).thenReturn(customFileEntity);

        transactionalFileUploader.uploadWithTransactionRollback(multipartFile, path);
        TransactionSynchronization synchronization = TransactionSynchronizationManager.getSynchronizations().getFirst();
        synchronization.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);

        verify(fileService).delete(customFileEntity);
        verify(s3BucketService).delete(
                filePath,
                uuid,
                fileExtension
        );
    }

    @Test
    void uploadWithTransactionRollback_shouldRegisterExactlyOneSynchronization_whenCalled() {
        String path = "/uploads/test";
        FileEntity fileEntity = FileFixtures.file();
        
        when(fileService.upload(any(MultipartFile.class), anyString())).thenReturn(fileEntity);

        transactionalFileUploader.uploadWithTransactionRollback(multipartFile, path);

        assertThat(TransactionSynchronizationManager.getSynchronizations())
                .hasSize(1)
                .first()
                .isInstanceOf(TransactionSynchronization.class);
    }

    @Test
    void uploadWithTransactionRollback_shouldDeleteInCorrectOrder_whenTransactionRollsBack() {
        String path = "/uploads/test";
        FileEntity fileEntity = FileFixtures.file();
        
        when(fileService.upload(any(MultipartFile.class), anyString())).thenReturn(fileEntity);

        transactionalFileUploader.uploadWithTransactionRollback(multipartFile, path);
        TransactionSynchronization synchronization = TransactionSynchronizationManager.getSynchronizations().getFirst();

        InOrder inOrder = inOrder(fileService, s3BucketService);

        synchronization.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);

        inOrder.verify(fileService).delete(fileEntity);
        inOrder.verify(s3BucketService).delete(
                "filePath",
                "uuid",
                "fileExtension"
        );
    }
}