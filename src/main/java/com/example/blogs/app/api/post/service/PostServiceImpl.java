package com.example.blogs.app.api.post.service;

import com.example.blogs.app.api.comment.entity.CommentEntity;
import com.example.blogs.app.api.comment.service.CommentService;
import com.example.blogs.app.api.file.entity.FileEntity;
import com.example.blogs.app.api.post.exception.FailedToCreatePostException;
import com.example.blogs.app.storage.FileLinkBuilder;
import com.example.blogs.app.api.file.service.FileService;
import com.example.blogs.app.api.post.dto.*;
import com.example.blogs.app.api.post.entity.PostEntity;
import com.example.blogs.app.api.post.mapper.PostMapper;
import com.example.blogs.app.api.post.repository.adapter.PostRepositoryAdapter;
import com.example.blogs.app.api.user.entity.UserEntity;
import com.example.blogs.app.api.user.mapper.UserMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    private final FileService fileService;

    private final FileLinkBuilder fileLinkBuilder;

    private final UserMapper userMapper;

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

        return postMapper.toPostDTO(post, comments);
    }

    /**
     * Updates a post by its ID with partial field updates.
     * Regenerates slug when title is updated to maintain URL consistency.
     *
     * @param postId     the ID of the post to update
     * @param requestDTO the update request containing fields to update
     * @return updated post details with new timestamp
     */
    @Override
    public PostUpdateResponseDTO updatePostById(long postId, PostUpdateRequestDTO requestDTO) {
        PostEntity post = postRepositoryAdapter.findById(postId);

        if (requestDTO.title() != null && !requestDTO.title().isBlank()) {
            post.setSlug(slugService.generate(requestDTO.title()));
        }

        PostEntity updatedPost = postMapper.toPostEntity(requestDTO, post);
        PostEntity savedPost = postRepositoryAdapter.update(updatedPost);

        return postMapper.toPostUpdateResponseDTO(savedPost);
    }

    @Override
    @SneakyThrows
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

            return postMapper.toPostCreateResponseDTO(post, previewImageUrl);
        } catch (Exception e) {
            throw new FailedToCreatePostException(e);
        }
    }
}
