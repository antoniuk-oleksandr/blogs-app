package com.example.blogs.app.api.post.service;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.fixture.CommentFixtures;
import com.example.blogs.app.api.comment.service.CommentService;
import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.exception.FailedToUploadFileException;
import com.example.blogs.app.api.file.fixture.FileFixtures;
import com.example.blogs.app.api.file.service.FileUrlBuilder;
import com.example.blogs.app.api.post.exception.FailedToCreatePostException;
import com.example.blogs.app.storage.FileLinkBuilder;
import com.example.blogs.app.api.file.service.FileService;
import com.example.blogs.app.api.post.dto.*;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.fixture.PostFixtures;
import com.example.blogs.app.api.post.mapper.PostMapper;
import com.example.blogs.app.api.post.repository.adapter.PostRepositoryAdapter;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.fixture.UserFixtures;
import com.example.blogs.app.api.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepositoryAdapter postRepositoryAdapter;

    @Mock
    private CommentService commentService;

    @Mock
    private PostMapper postMapper;

    @Mock
    private SlugService slugService;

    @Mock
    private FileService fileService;

    @Mock
    private FileLinkBuilder fileLinkBuilder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private TransactionalFileUploader transactionalFileUploader;

    @Mock
    private PostSlugUpdater postSlugUpdater;

    @Mock
    private FileUrlBuilder fileUrlBuilder;

    @Mock
    private MultipartFile multipartFile;

    private PostService postService;

    @BeforeEach
    void setUp() {
        postService = new PostServiceImpl(
                postRepositoryAdapter,
                commentService,
                postMapper,
                slugService,
                transactionalFileUploader,
                fileService,
                fileLinkBuilder,
                userMapper,
                postSlugUpdater,
                fileUrlBuilder
        );
    }

    @Test
    void getPostsByUserId_shouldReturnPostsForGivenUserId() {
        UserEntity mockUser = UserEntity.builder().id(1L).username("testuser").build();
        List<PostEntity> mockPosts = List.of(
                PostEntity.builder()
                        .id(1L).author(mockUser).title("Post 1").content("Content 1").build(),
                PostEntity.builder()
                        .id(2L).author(mockUser).title("Post 2").content("Content 2").build()
        );

        when(postRepositoryAdapter.findByAuthorId(anyLong())).thenReturn(mockPosts);

        List<PostEntity> result = postService.getPostsByUserId(1L);
        assertThat(result).isEqualTo(mockPosts);
        verify(postRepositoryAdapter, times(1)).findByAuthorId(1L);
    }

    @Test
    void deletePostById_shouldInvokeRepositoryDelete() {
        Long postId = 1L;

        postService.deletePostById(postId);

        verify(postRepositoryAdapter).deleteById(postId);
    }

    @Test
    void getPostBySlug_shouldReturnPostDTO() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(1L, now);
        FileEntity file = FileFixtures.file();
        PostEntity post = PostFixtures.post(1L, now, author, file);
        List<CommentEntity> comments = List.of(
                CommentFixtures.comment(1L, now, author, post),
                CommentFixtures.comment(2L, now, author, post)
        );
        PostUserSummaryDTO authorDTO = PostFixtures.postUserSummaryDTO(author.getId());
        List<PostCommentSummaryDTO> commentDTOs = List.of(
                PostFixtures.postCommentSummaryDTO(1L, now, authorDTO),
                PostFixtures.postCommentSummaryDTO(2L, now, authorDTO)
        );
        PostDTO expectedDTO = PostFixtures.postDTO(1L, now, authorDTO, commentDTOs);
        String previewImageUrl = "previewImageUrl";
        when(postRepositoryAdapter.findBySlug(anyString())).thenReturn(post);
        when(fileLinkBuilder.buildLink(anyString(), anyString(), anyString()))
                .thenReturn(previewImageUrl);
        when(commentService.getCommentsByPostId(post.getId())).thenReturn(comments);
        when(postMapper.toPostDTO(post, comments, previewImageUrl)).thenReturn(expectedDTO);

        PostDTO result = postService.getPostBySlug(post.getSlug());

        assertThat(result)
                .isNotNull()
                .isEqualTo(expectedDTO)
                .satisfies(dto -> {
                    assertThat(dto.id()).isEqualTo(1L);
                    assertThat(dto.slug()).isEqualTo(post.getSlug());
                });
        verify(postRepositoryAdapter).findBySlug(post.getSlug());
        verify(commentService).getCommentsByPostId(1L);
        verify(postMapper).toPostDTO(post, comments, previewImageUrl);
        verify(fileLinkBuilder).buildLink(
                post.getFile().getFilePath(),
                post.getFile().getUuid(),
                post.getFile().getFileExtension()
        );
        verifyNoMoreInteractions(postRepositoryAdapter, commentService, postMapper);
    }

    @Test
    void createPost_shouldReturnPostCreateResponseDTO() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        PostCreateRequestDTO requestDTO = new PostCreateRequestDTO(
                "title",
                "description",
                "content"
        );
        Long authorId = 1L;
        long postId = 1L;
        UserEntity author = UserFixtures.user(authorId, now);
        FileEntity fileEntity = FileFixtures.file();
        String previewImageUrl = "previewImageUrl";
        String generatedSlug = "slug";
        PostEntity newPost = PostFixtures.post(postId, now, author);
        PostCreateResponseDTO expectedResponse = new PostCreateResponseDTO(
                postId,
                requestDTO.title(),
                requestDTO.description(),
                requestDTO.content(),
                generatedSlug,
                previewImageUrl,
                now
        );

        when(fileService.upload(any(), eq("posts"))).thenReturn(fileEntity);
        when(fileLinkBuilder.buildLink(anyString(), anyString(), anyString()))
                .thenReturn(previewImageUrl);
        when(slugService.generate(anyString())).thenReturn(generatedSlug);
        when(userMapper.toUserEntity(anyLong())).thenReturn(author);
        when(postMapper.toPostEntity(
                any(PostCreateRequestDTO.class),
                anyString(),
                any(FileEntity.class),
                any(UserEntity.class)
        )).thenReturn(newPost);
        when(postRepositoryAdapter.save(newPost)).thenReturn(newPost);
        when(postMapper.toPostCreateResponseDTO(any(PostEntity.class), anyString()))
                .thenReturn(expectedResponse);

        PostCreateResponseDTO response = postService.createPost(authorId, requestDTO, null);

        assertThat(response).isEqualTo(expectedResponse);
        verify(fileService).upload(any(), eq("posts"));
        verify(fileLinkBuilder).buildLink(
                fileEntity.getFilePath(),
                fileEntity.getUuid(),
                fileEntity.getFileExtension()
        );
        verify(slugService).generate(requestDTO.title());
        verify(userMapper).toUserEntity(authorId);
        verify(postMapper).toPostEntity(requestDTO, generatedSlug, fileEntity, author);
        verify(postRepositoryAdapter).save(newPost);
        verify(postMapper).toPostCreateResponseDTO(newPost, previewImageUrl);
    }

    @Test
    void createPost_shouldThrowFailedToCreatePost_whenAnythingFails() {
        PostCreateRequestDTO requestDTO = PostFixtures.postCreateRequestDTO();
        long authorId = 1L;

        when(fileService.upload(any(), anyString())).thenThrow(new FailedToUploadFileException(null));

        assertThatThrownBy(() -> postService.createPost(authorId, requestDTO, null))
                .isInstanceOf(FailedToCreatePostException.class)
                .hasMessageContaining("Failed to create post");

        verify(fileService).upload(any(), eq("posts"));
        verifyNoMoreInteractions(fileLinkBuilder, slugService, userMapper, postMapper, postRepositoryAdapter);
    }

    @Test
    void updatePostById_shouldUploadNewFileAndDeleteOld_whenPreviewImageIsProvided() {
        Long authorId = 1L;
        Long postId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(authorId, now);
        FileEntity oldFile = FileFixtures.file(1L, now);
        FileEntity newFile = FileFixtures.file(2L, now);
        PostEntity existingPost = PostFixtures.post(postId, now, author, oldFile);
        String newPreviewImageUrl = "newPreviewImageUrl";
        String newSlug = "updated-title";
        PostUpdateRequestDTO request = new PostUpdateRequestDTO(
                "Updated Title",
                "Updated Description",
                "Updated Content"
        );
        existingPost.setSlug(newSlug);
        PostEntity updatedPost = PostEntity.builder()
                .id(postId)
                .author(author)
                .title(request.title())
                .description(request.description())
                .content(request.content())
                .slug(newSlug)
                .file(newFile)
                .createdAt(existingPost.getCreatedAt())
                .build();
        PostUpdateResponseDTO expectedResponse = new PostUpdateResponseDTO(
                postId,
                request.title(),
                request.description(),
                request.content(),
                newSlug,
                newPreviewImageUrl,
                now
        );

        when(postRepositoryAdapter.findById(postId)).thenReturn(existingPost);
        when(transactionalFileUploader.uploadWithTransactionRollback(multipartFile, "posts"))
                .thenReturn(newFile);
        when(fileLinkBuilder.buildLink(
                newFile.getFilePath(),
                newFile.getUuid(),
                newFile.getFileExtension()
        )).thenReturn(newPreviewImageUrl);
        when(postMapper.toPostEntity(request, existingPost)).thenReturn(updatedPost);
        when(postRepositoryAdapter.update(updatedPost)).thenReturn(updatedPost);
        when(postMapper.toPostUpdateResponseDTO(updatedPost, newPreviewImageUrl))
                .thenReturn(expectedResponse);

        PostUpdateResponseDTO response = postService.updatePostById(postId, request, multipartFile);

        assertThat(response).isEqualTo(expectedResponse);
        verify(postRepositoryAdapter).findById(postId);
        verify(postSlugUpdater).apply(existingPost, request);
        verify(transactionalFileUploader).uploadWithTransactionRollback(multipartFile, "posts");
        verify(fileLinkBuilder).buildLink(
                newFile.getFilePath(),
                newFile.getUuid(),
                newFile.getFileExtension()
        );
        verify(postMapper).toPostEntity(request, existingPost);
        verify(postRepositoryAdapter).update(updatedPost);
        verify(postMapper).toPostUpdateResponseDTO(updatedPost, newPreviewImageUrl);
        verify(fileService).delete(oldFile);
    }

    @Test
    void updatePostById_shouldGenerateNewSlugButKeepOldFile_whenTitleUpdatedButNoNewFile() {
        Long authorId = 1L;
        Long postId = 1L;
        String previewImageUrl = "previewImageUrl";
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(authorId, now);
        FileEntity fileEntity = FileFixtures.file();
        PostEntity existingPost = PostFixtures.post(postId, now, author, fileEntity);
        String newSlug = "updated-title";
        PostUpdateRequestDTO request = new PostUpdateRequestDTO(
                "Updated Title",
                "Updated Description",
                "Updated Content"
        );
        existingPost.setSlug(newSlug);
        PostEntity updatedPost = PostEntity.builder()
                .id(postId)
                .author(author)
                .title(request.title())
                .description(request.description())
                .content(request.content())
                .slug(newSlug)
                .file(fileEntity)
                .createdAt(existingPost.getCreatedAt())
                .build();
        PostUpdateResponseDTO expectedResponse = new PostUpdateResponseDTO(
                postId,
                request.title(),
                request.description(),
                request.content(),
                newSlug,
                previewImageUrl,
                now
        );

        when(postRepositoryAdapter.findById(postId)).thenReturn(existingPost);
        when(fileUrlBuilder.build(fileEntity)).thenReturn(previewImageUrl);
        when(postMapper.toPostEntity(request, existingPost)).thenReturn(updatedPost);
        when(postRepositoryAdapter.update(updatedPost)).thenReturn(updatedPost);
        when(postMapper.toPostUpdateResponseDTO(updatedPost, previewImageUrl))
                .thenReturn(expectedResponse);

        PostUpdateResponseDTO response = postService.updatePostById(postId, request, null);

        assertThat(response).isEqualTo(expectedResponse);
        verify(postRepositoryAdapter).findById(postId);
        verify(postSlugUpdater).apply(existingPost, request);
        verify(fileUrlBuilder).build(fileEntity);
        verify(postMapper).toPostEntity(request, existingPost);
        verify(postRepositoryAdapter).update(updatedPost);
        verify(postMapper).toPostUpdateResponseDTO(updatedPost, previewImageUrl);
        verify(transactionalFileUploader, never()).uploadWithTransactionRollback(any(), any());
        verify(fileService, never()).delete(any());
    }

    @Test
    void updatePostById_shouldReturnPostUpdateResponseDTO_whenTitleIsNullAndNoNewFile() {
        Long authorId = 1L;
        Long postId = 1L;
        String previewImageUrl = "previewImageUrl";
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(authorId, now);
        FileEntity fileEntity = FileFixtures.file();
        PostEntity existingPost = PostFixtures.post(postId, now, author, fileEntity);
        PostUpdateRequestDTO request = new PostUpdateRequestDTO(
                null,
                "Updated Description",
                "Updated Content"
        );
        PostEntity updatedPost = PostEntity.builder()
                .id(postId)
                .author(author)
                .title(existingPost.getTitle())
                .description(request.description())
                .content(request.content())
                .slug(existingPost.getSlug())
                .file(fileEntity)
                .createdAt(existingPost.getCreatedAt())
                .build();
        PostUpdateResponseDTO expectedResponse = new PostUpdateResponseDTO(
                postId,
                existingPost.getTitle(),
                request.description(),
                request.content(),
                existingPost.getSlug(),
                previewImageUrl,
                now
        );

        when(postRepositoryAdapter.findById(postId)).thenReturn(existingPost);
        when(fileUrlBuilder.build(fileEntity)).thenReturn(previewImageUrl);
        when(postMapper.toPostEntity(request, existingPost)).thenReturn(updatedPost);
        when(postRepositoryAdapter.update(updatedPost)).thenReturn(updatedPost);
        when(postMapper.toPostUpdateResponseDTO(updatedPost, previewImageUrl))
                .thenReturn(expectedResponse);

        PostUpdateResponseDTO response = postService.updatePostById(postId, request, null);

        assertThat(response).isEqualTo(expectedResponse);
        verify(postRepositoryAdapter).findById(postId);
        verify(postSlugUpdater).apply(existingPost, request);
        verify(fileUrlBuilder).build(fileEntity);
        verify(postMapper).toPostEntity(request, existingPost);
        verify(postRepositoryAdapter).update(updatedPost);
        verify(postMapper).toPostUpdateResponseDTO(updatedPost, previewImageUrl);
        verify(transactionalFileUploader, never()).uploadWithTransactionRollback(any(), any());
        verify(fileService, never()).delete(any());
        verify(slugService, never()).generate(any());
    }

    @Test
    void updatePostById_shouldNotGenerateSlug_whenTitleIsBlank() {
        Long authorId = 1L;
        Long postId = 1L;
        String previewImageUrl = "previewImageUrl";
        LocalDateTime now = LocalDateTime.now().withNano(0);
        UserEntity author = UserFixtures.user(authorId, now);
        FileEntity fileEntity = FileFixtures.file();
        PostEntity existingPost = PostFixtures.post(postId, now, author, fileEntity);
        PostUpdateRequestDTO request = new PostUpdateRequestDTO(
                "",
                "Updated Description",
                "Updated Content"
        );
        PostEntity updatedPost = PostEntity.builder()
                .id(postId)
                .author(author)
                .title(existingPost.getTitle())
                .description(request.description())
                .content(request.content())
                .slug(existingPost.getSlug())
                .file(fileEntity)
                .createdAt(existingPost.getCreatedAt())
                .build();
        PostUpdateResponseDTO expectedResponse = new PostUpdateResponseDTO(
                postId,
                existingPost.getTitle(),
                request.description(),
                request.content(),
                existingPost.getSlug(),
                previewImageUrl,
                now
        );

        when(postRepositoryAdapter.findById(postId)).thenReturn(existingPost);
        when(fileUrlBuilder.build(fileEntity)).thenReturn(previewImageUrl);
        when(postMapper.toPostEntity(request, existingPost)).thenReturn(updatedPost);
        when(postRepositoryAdapter.update(updatedPost)).thenReturn(updatedPost);
        when(postMapper.toPostUpdateResponseDTO(updatedPost, previewImageUrl))
                .thenReturn(expectedResponse);

        PostUpdateResponseDTO response = postService.updatePostById(postId, request, null);

        assertThat(response).isEqualTo(expectedResponse);
        verify(postRepositoryAdapter).findById(postId);
        verify(postSlugUpdater).apply(existingPost, request);
        verify(fileUrlBuilder).build(fileEntity);
        verify(postMapper).toPostEntity(request, existingPost);
        verify(postRepositoryAdapter).update(updatedPost);
        verify(postMapper).toPostUpdateResponseDTO(updatedPost, previewImageUrl);
        verify(slugService, never()).generate(any());
        verify(transactionalFileUploader, never()).uploadWithTransactionRollback(any(), any());
        verify(fileService, never()).delete(any());
    }
}
