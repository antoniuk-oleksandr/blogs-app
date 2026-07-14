package com.example.blogs.app.api.post.service;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.fixture.CommentFixtures;
import com.example.blogs.app.api.comment.service.CommentService;
import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.exception.FailedToUploadFileException;
import com.example.blogs.app.api.file.fixture.FileFixtures;
import com.example.blogs.app.api.file.service.FileUrlBuilder;
import com.example.blogs.app.api.post.exception.FailedToCreatePostException;
import com.example.blogs.app.api.file.service.FileService;
import com.example.blogs.app.api.post.dto.*;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.fixture.PostFixtures;
import com.example.blogs.app.api.post.mapper.PostMapper;
import com.example.blogs.app.api.post.repository.adapter.PostRepositoryAdapter;
import com.example.blogs.app.api.search.indexing.PostSearchIndexEventPublisher;
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
    private UserMapper userMapper;

    @Mock
    private TransactionalFileUploader transactionalFileUploader;

    @Mock
    private PostSlugUpdater postSlugUpdater;

    @Mock
    private FileUrlBuilder fileUrlBuilder;

    @Mock
    private PostSearchIndexEventPublisher searchIndexEventPublisher;

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
                userMapper,
                postSlugUpdater,
                fileUrlBuilder,
                searchIndexEventPublisher
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
        verify(searchIndexEventPublisher).publishDeleteAfterCommit(postId);
    }

    @Test
    void getPostBySlug_shouldReturnPostDTO() {
        Long userId = 1L;
        Long postId = 1L;
        Long fileId = 1L;
        Long firstCommentId = 1L;
        Long secondCommentId = 2L;
        String previewImageUrl = "previewImageUrl";

        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity author = UserFixtures.user(userId, file, now);
        PostEntity post = PostFixtures.post(postId, now, author, file);

        List<CommentEntity> comments = List.of(
                CommentFixtures.commentEntity(firstCommentId, now, author, post),
                CommentFixtures.commentEntity(secondCommentId, now, author, post)
        );
        PostUserSummaryDTO authorDTO = PostFixtures.postUserSummaryDTO(author.getId());
        List<PostCommentSummaryDTO> commentDTOs = List.of(
                PostFixtures.postCommentSummaryDTO(firstCommentId, now, authorDTO),
                PostFixtures.postCommentSummaryDTO(secondCommentId, now, authorDTO)
        );
        PostDTO expectedDTO = PostFixtures.postDTO(postId, now, authorDTO, commentDTOs);
        when(postRepositoryAdapter.findBySlug(anyString())).thenReturn(post);
        when(fileUrlBuilder.build(any(FileEntity.class)))
                .thenReturn(previewImageUrl);
        when(commentService.getCommentsByPostId(post.getId())).thenReturn(comments);
        when(postMapper.toPostDTO(post, comments, previewImageUrl, previewImageUrl))
                .thenReturn(expectedDTO);

        PostDTO result = postService.getPostBySlug(post.getSlug());

        assertThat(result)
                .isNotNull()
                .isEqualTo(expectedDTO)
                .satisfies(dto -> {
                    assertThat(dto.id()).isEqualTo(postId);
                    assertThat(dto.slug()).isEqualTo(post.getSlug());
                });
        verify(postRepositoryAdapter).findBySlug(post.getSlug());
        verify(commentService).getCommentsByPostId(postId);
        verify(postMapper).toPostDTO(post, comments, previewImageUrl, previewImageUrl);
        verify(fileUrlBuilder, times(2)).build(file);
        verifyNoMoreInteractions(postRepositoryAdapter, commentService, postMapper);
    }

    @Test
    void createPost_shouldReturnPostCreateResponseDTO() {
        Long userId = 1L;
        Long postId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity author = UserFixtures.user(userId, file, now);
        PostEntity post = PostFixtures.post(postId, now, author);
        PostCreateRequestDTO requestDTO = PostFixtures.postCreateRequestDTO();
        String previewImageUrl = "previewImageUrl";
        String generatedSlug = "slug";
        PostCreateResponseDTO expectedResponse = PostFixtures.postCreateResponseDTO(postId, now);

        when(fileService.upload(any(), eq("posts"))).thenReturn(file);
        when(fileUrlBuilder.build(any(FileEntity.class)))
                .thenReturn(previewImageUrl);
        when(slugService.generate(anyString())).thenReturn(generatedSlug);
        when(userMapper.toUserEntity(anyLong())).thenReturn(author);
        when(postMapper.toPostEntity(
                any(PostCreateRequestDTO.class),
                anyString(),
                any(FileEntity.class),
                any(UserEntity.class)
        )).thenReturn(post);
        when(postRepositoryAdapter.save(post)).thenReturn(post);
        when(postMapper.toPostCreateResponseDTO(any(PostEntity.class), anyString()))
                .thenReturn(expectedResponse);

        PostCreateResponseDTO response = postService.createPost(userId, requestDTO, null);

        assertThat(response).isEqualTo(expectedResponse);
        verify(fileService).upload(any(), eq("posts"));
        verify(fileUrlBuilder).build(file);
        verify(slugService).generate(requestDTO.title());
        verify(userMapper).toUserEntity(userId);
        verify(postMapper).toPostEntity(requestDTO, generatedSlug, file, author);
        verify(postRepositoryAdapter).save(post);
        verify(searchIndexEventPublisher).publishUpsertAfterCommit(postId);
        verify(postMapper).toPostCreateResponseDTO(post, previewImageUrl);
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
        verifyNoMoreInteractions(fileUrlBuilder, slugService, userMapper, postMapper, postRepositoryAdapter);
    }

    @Test
    void updatePostById_shouldUploadNewFileAndDeleteOld_whenPreviewImageIsProvided() {
        Long userId = 1L;
        long postId = 1L;
        Long oldFileId = 1L;
        Long newFileId = 2L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity oldFile = FileFixtures.file(oldFileId, now);
        UserEntity author = UserFixtures.user(userId, oldFile, now);
        FileEntity newFile = FileFixtures.file(newFileId, now);
        PostEntity existingPost = PostFixtures.post(postId, now, author, oldFile);
        String newPreviewImageUrl = "newPreviewImageUrl";
        String newSlug = "updated-title";
        PostUpdateRequestDTO request = PostFixtures.postUpdateRequestDTO();
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
        when(fileUrlBuilder.build(any(FileEntity.class))).thenReturn(newPreviewImageUrl);
        when(postMapper.toPostEntity(request, existingPost)).thenReturn(updatedPost);
        when(postRepositoryAdapter.update(updatedPost)).thenReturn(updatedPost);
        when(postMapper.toPostUpdateResponseDTO(updatedPost, newPreviewImageUrl))
                .thenReturn(expectedResponse);

        PostUpdateResponseDTO response = postService.updatePostById(postId, request, multipartFile);

        assertThat(response).isEqualTo(expectedResponse);
        verify(postRepositoryAdapter).findById(postId);
        verify(postSlugUpdater).apply(existingPost, request);
        verify(transactionalFileUploader).uploadWithTransactionRollback(multipartFile, "posts");
        verify(fileUrlBuilder).build(newFile);
        verify(postMapper).toPostEntity(request, existingPost);
        verify(postRepositoryAdapter).update(updatedPost);
        verify(searchIndexEventPublisher).publishUpsertAfterCommit(postId);
        verify(postMapper).toPostUpdateResponseDTO(updatedPost, newPreviewImageUrl);
        verify(fileService).delete(oldFile);
    }

    @Test
    void updatePostById_shouldGenerateNewSlugButKeepOldFile_whenTitleUpdatedButNoNewFile() {
        Long userId = 1L;
        long postId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity author = UserFixtures.user(userId, file, now);
        PostEntity existingPost = PostFixtures.post(postId, now, author);
        String newSlug = "updated-title";
        PostUpdateRequestDTO request = PostFixtures.postUpdateRequestDTO();
        existingPost.setSlug(newSlug);
        PostEntity updatedPost = PostEntity.builder()
                .id(postId)
                .author(author)
                .title(request.title())
                .description(request.description())
                .content(request.content())
                .slug(newSlug)
                .file(file)
                .createdAt(existingPost.getCreatedAt())
                .build();
        PostUpdateResponseDTO expectedResponse = new PostUpdateResponseDTO(
                postId,
                request.title(),
                request.description(),
                request.content(),
                newSlug,
                null,
                now
        );

        when(postRepositoryAdapter.findById(postId)).thenReturn(existingPost);
        when(postMapper.toPostEntity(request, existingPost)).thenReturn(updatedPost);
        when(postRepositoryAdapter.update(updatedPost)).thenReturn(updatedPost);
        when(postMapper.toPostUpdateResponseDTO(
                eq(updatedPost),
                any()
        )).thenReturn(expectedResponse);
        PostUpdateResponseDTO response = postService.updatePostById(postId, request, null);

        assertThat(response).isEqualTo(expectedResponse);
        verify(postRepositoryAdapter).findById(postId);
        verify(postSlugUpdater).apply(existingPost, request);
        verify(fileUrlBuilder, never()).build(any(FileEntity.class));
        verify(postMapper).toPostEntity(request, existingPost);
        verify(postRepositoryAdapter).update(updatedPost);
        verify(searchIndexEventPublisher).publishUpsertAfterCommit(postId);
        verify(postMapper).toPostUpdateResponseDTO(updatedPost, null);
        verify(transactionalFileUploader, never()).uploadWithTransactionRollback(any(), any());
        verify(fileService, never()).delete(any());
    }

    @Test
    void updatePostById_shouldReturnPostUpdateResponseDTO_whenTitleIsNullAndNoNewFile() {
        Long userId = 1L;
        long postId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity author = UserFixtures.user(userId, file, now);
        PostEntity existingPost = PostFixtures.post(postId, now, author);
        PostUpdateRequestDTO request = PostFixtures.postUpdateRequestDTO();
        PostEntity updatedPost = PostEntity.builder()
                .id(postId)
                .author(author)
                .title(existingPost.getTitle())
                .description(request.description())
                .content(request.content())
                .slug(existingPost.getSlug())
                .file(file)
                .createdAt(existingPost.getCreatedAt())
                .build();
        PostUpdateResponseDTO expectedResponse = new PostUpdateResponseDTO(
                postId,
                existingPost.getTitle(),
                request.description(),
                request.content(),
                existingPost.getSlug(),
                null,
                now
        );

        when(postRepositoryAdapter.findById(postId)).thenReturn(existingPost);
        when(postMapper.toPostEntity(request, existingPost)).thenReturn(updatedPost);
        when(postRepositoryAdapter.update(updatedPost)).thenReturn(updatedPost);
        when(postMapper.toPostUpdateResponseDTO(
                eq(updatedPost),
                any()
        )).thenReturn(expectedResponse);

        PostUpdateResponseDTO response = postService.updatePostById(postId, request, null);

        assertThat(response).isEqualTo(expectedResponse);
        verify(postRepositoryAdapter).findById(postId);
        verify(postSlugUpdater).apply(existingPost, request);
        verify(fileUrlBuilder, never()).build(any(FileEntity.class));
        verify(postMapper).toPostEntity(request, existingPost);
        verify(postRepositoryAdapter).update(updatedPost);
        verify(searchIndexEventPublisher).publishUpsertAfterCommit(postId);
        verify(postMapper).toPostUpdateResponseDTO(updatedPost, null);
        verify(transactionalFileUploader, never()).uploadWithTransactionRollback(any(), any());
        verify(fileService, never()).delete(any());
        verify(slugService, never()).generate(any());
    }

    @Test
    void updatePostById_shouldNotGenerateSlug_whenTitleIsBlank() {
        Long userId = 1L;
        long postId = 1L;
        Long fileId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);
        FileEntity file = FileFixtures.file(fileId, now);
        UserEntity author = UserFixtures.user(userId, file, now);
        PostEntity existingPost = PostFixtures.post(postId, now, author);
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
                .file(file)
                .createdAt(existingPost.getCreatedAt())
                .build();
        PostUpdateResponseDTO expectedResponse = new PostUpdateResponseDTO(
                postId,
                existingPost.getTitle(),
                request.description(),
                request.content(),
                existingPost.getSlug(),
                null,
                now
        );

        when(postRepositoryAdapter.findById(postId)).thenReturn(existingPost);
        when(postMapper.toPostEntity(request, existingPost)).thenReturn(updatedPost);
        when(postRepositoryAdapter.update(updatedPost)).thenReturn(updatedPost);
        when(postMapper.toPostUpdateResponseDTO(
                eq(updatedPost),
                any()
        )).thenReturn(expectedResponse);

        PostUpdateResponseDTO response = postService.updatePostById(postId, request, null);

        assertThat(response).isEqualTo(expectedResponse);
        verify(postRepositoryAdapter).findById(postId);
        verify(postSlugUpdater).apply(existingPost, request);
        verify(fileUrlBuilder, never()).build(any(FileEntity.class));
        verify(postMapper).toPostEntity(request, existingPost);
        verify(postRepositoryAdapter).update(updatedPost);
        verify(searchIndexEventPublisher).publishUpsertAfterCommit(postId);
        verify(postMapper).toPostUpdateResponseDTO(updatedPost, null);
        verify(slugService, never()).generate(any());
        verify(transactionalFileUploader, never()).uploadWithTransactionRollback(any(), any());
        verify(fileService, never()).delete(any());
    }
}
