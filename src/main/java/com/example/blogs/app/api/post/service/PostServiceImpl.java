package com.example.blogs.app.api.post.service;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.service.CommentService;
import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.file.service.FileUrlBuilder;
import com.example.blogs.app.api.post.exception.FailedToCreatePostException;
import com.example.blogs.app.logging.MDCKeys;
import com.example.blogs.app.storage.FileLinkBuilder;
import com.example.blogs.app.api.file.service.FileService;
import com.example.blogs.app.api.post.dto.*;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.mapper.PostMapper;
import com.example.blogs.app.api.post.repository.adapter.PostRepositoryAdapter;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Orchestrates post retrieval operations by coordinating with the post repository adapter.
 */
@Service
@AllArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepositoryAdapter postRepositoryAdapter;

    private final CommentService commentService;

    private final PostMapper postMapper;

    private final SlugService slugService;

    private final TransactionalFileUploader transactionalFileUploader;

    private final FileService fileService;

    private final FileLinkBuilder fileLinkBuilder;

    private final UserMapper userMapper;

    private final PostSlugUpdater postSlugUpdater;

    private final FileUrlBuilder fileUrlBuilder;

    private static final Logger log = LoggerFactory.getLogger(PostServiceImpl.class);

    /**
     * Retrieves all posts created by the specified user.
     * Delegates to the repository adapter for data retrieval.
     *
     * @param userId the ID of the user
     * @return list of posts created by the user
     */
    @Override
    public List<PostEntity> getPostsByUserId(long userId) {
        return postRepositoryAdapter.findByAuthorId(userId);
    }

    /**
     * Deletes a post by its ID.
     * Delegates to the repository adapter for deletion.
     *
     * @param postId the ID of the post to delete
     */
    @Override
    public void deletePostById(Long postId) {
        postRepositoryAdapter.deleteById(postId);
        log.info("post_deleted postId={} userId={} requestId={}",
                postId, MDC.get(MDCKeys.USER_ID), MDC.get(MDCKeys.REQUEST_ID));
    }

    /**
     * Retrieves a post by its unique slug identifier with associated comments.
     * Delegates to the repository adapter for post retrieval and comment service for comments.
     *
     * @param slug the unique slug of the post
     * @return post details with associated comments
     */
    @Override
    @Transactional(readOnly = true)
    public PostDTO getPostBySlug(String slug) {
        PostEntity post = postRepositoryAdapter.findBySlug(slug);
        List<CommentEntity> comments = commentService.getCommentsByPostId(post.getId());

        String previewImageUrl = Optional.ofNullable(post.getFile())
                .map(file -> fileLinkBuilder.buildLink(
                        file.getFilePath(),
                        file.getUuid(),
                        file.getFileExtension()
                ))
                .orElse(null);

        log.info("post_viewed postId={} slug={} commentCount={} requestId={}",
                post.getId(), slug, comments.size(), MDC.get(MDCKeys.REQUEST_ID));

        return postMapper.toPostDTO(post, comments, previewImageUrl);
    }

    /**
     * Updates a post by its ID with partial field updates.
     * Regenerates slug when title is updated to maintain URL consistency.
     *
     * @param postId       the ID of the post to update
     * @param requestDTO   the update request containing fields to update
     * @param previewImage the new preview image file to upload (optional)
     * @return updated post details with new timestamp
     */
    @Override
    @Transactional
    public PostUpdateResponseDTO updatePostById(
            long postId, PostUpdateRequestDTO requestDTO, MultipartFile previewImage
    ) {
        PostEntity post = postRepositoryAdapter.findById(postId);
        FileEntity oldFile = post.getFile();

        postSlugUpdater.apply(post, requestDTO);

        Optional<FileEntity> newFile = Optional.ofNullable(previewImage)
                .map(image -> transactionalFileUploader
                        .uploadWithTransactionRollback(image, "posts"));
        newFile.ifPresent(post::setFile);

        String previewImageUrl = newFile
                .map(f -> fileLinkBuilder.buildLink(
                        f.getFilePath(),
                        f.getUuid(),
                        f.getFileExtension()
                ))
                .orElseGet(() -> fileUrlBuilder.build(post.getFile()));

        PostEntity updatedPost = postMapper.toPostEntity(requestDTO, post);
        PostEntity savedPost = postRepositoryAdapter.update(updatedPost);

        newFile.ifPresent(file -> {
            fileService.delete(oldFile);
            log.info("preview_image_updated postId={} oldFileId={} newFileId={} userId={} requestId={}",
                    postId, oldFile.getId(), file.getId(),
                    MDC.get(MDCKeys.USER_ID), MDC.get(MDCKeys.REQUEST_ID));
        });

        log.info("post_updated postId={} userId={} requestId={}",
                postId, MDC.get(MDCKeys.USER_ID), MDC.get(MDCKeys.REQUEST_ID));

        return postMapper.toPostUpdateResponseDTO(savedPost, previewImageUrl);
    }

    /**
     * Creates a new post with a preview image and generates a unique slug.
     * Uploads the preview image to storage, saves the post entity, and returns the complete post details.
     *
     * @param authorId     the ID of the user creating the post
     * @param requestDTO   the post creation request containing title, description, and content
     * @param previewImage the preview image file to upload
     * @return created post details with generated slug and preview image URL
     * @throws FailedToCreatePostException if file upload or post creation fails
     */
    @Override
    @Transactional
    public PostCreateResponseDTO createPost(
            long authorId, PostCreateRequestDTO requestDTO, MultipartFile previewImage
    ) {
        try {
            FileEntity fileEntity = fileService.upload(previewImage, "posts");
            String previewImageUrl = fileLinkBuilder.buildLink(
                    fileEntity.getFilePath(),
                    fileEntity.getUuid(),
                    fileEntity.getFileExtension()
            );

            String slug = slugService.generate(requestDTO.title());
            UserEntity author = userMapper.toUserEntity(authorId);
            PostEntity createPostEntity = postMapper.toPostEntity(requestDTO, slug, fileEntity, author);
            PostEntity post = postRepositoryAdapter.save(createPostEntity);

            log.info("preview_image_uploaded postId={} fileId={} userId={} requestId={}",
                    post.getId(), fileEntity.getId(), authorId, MDC.get(MDCKeys.REQUEST_ID));
            log.info("post_created postId={} userId={} slug={} requestId={}",
                    post.getId(), authorId, slug, MDC.get(MDCKeys.REQUEST_ID));

            return postMapper.toPostCreateResponseDTO(post, previewImageUrl);
        } catch (Exception e) {
            log.error("post_creation_failed authorId={} title={} error={} requestId={}",
                    authorId, requestDTO.title(), e.getMessage(), MDC.get(MDCKeys.REQUEST_ID), e);
            throw new FailedToCreatePostException(e);
        }
    }
}
