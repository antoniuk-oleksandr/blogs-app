package com.example.blogs.app.api.reaction.controller;

import com.example.blogs.app.api.reaction.dto.ReactionDTO;
import com.example.blogs.app.api.reaction.dto.ReactionSetRequestDTO;
import com.example.blogs.app.api.reaction.service.ReactionService;
import com.example.blogs.app.security.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    @PostMapping("/posts/{postId}/reactions")
    public ResponseEntity<ReactionDTO> setReaction(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @NotNull @RequestBody ReactionSetRequestDTO requestDTO
    ) {
        return ResponseEntity
                .ok(reactionService.setReaction(
                        postId,
                        userPrincipal.id(),
                        requestDTO.reactionType()
                ));
    }
}
