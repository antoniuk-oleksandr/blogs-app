package com.example.blogs.app.api.reaction.mapper;

import com.example.blogs.app.api.reaction.dto.ReactionDTO;
import com.example.blogs.app.api.reaction.entity.ReactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Maps between reaction entities and DTOs using MapStruct.
 */
@Mapper(componentModel = "spring")
public interface ReactionMapper {

    /**
     * Converts a reaction entity to a reaction DTO.
     * Maps nested user and post IDs to their respective DTO fields.
     *
     * @param reactionEntity the reaction entity to convert
     * @return reaction DTO with all fields populated
     */
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "post.id", target = "postId")
    ReactionDTO toReactionDTO(ReactionEntity reactionEntity);
}
