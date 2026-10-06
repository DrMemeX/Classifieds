package ru.drmemex.classifieds.feature.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drmemex.classifieds.feature.comment.dto.CommentResponse;
import ru.drmemex.classifieds.feature.comment.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/comments")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping("/advertisements/{advertisementId}")
    public List<CommentResponse> getByAdvertisementId(
            @PathVariable
            Long advertisementId
    ) {
        return commentService.getByAdvertisementId(
                advertisementId
        );
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable
            Long commentId
    ) {
        commentService.delete(commentId);
    }
}