package ru.drmemex.classifieds.feature.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.common.util.pagination.dto.PageResponse;
import ru.drmemex.classifieds.feature.comment.dto.CommentRequest;
import ru.drmemex.classifieds.feature.comment.dto.CommentResponse;
import ru.drmemex.classifieds.feature.comment.service.CommentService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@PreAuthorize("hasRole('USER')")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/advertisements/{advertisementId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse create(
            @PathVariable
            Long advertisementId,

            @Valid @RequestBody
            CommentRequest request
    ) {
        return commentService.create(
                advertisementId,
                request
        );
    }

    @GetMapping("/advertisements/{advertisementId}")
    public PageResponse<CommentResponse> getByAdvertisementId(
            @PathVariable
            Long advertisementId,

            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "20")
            @Min(1)
            @Max(100)
            int size
    ) {
        return commentService.getByAdvertisementId(
                advertisementId,
                new PageRequest(
                        page,
                        size
                )
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