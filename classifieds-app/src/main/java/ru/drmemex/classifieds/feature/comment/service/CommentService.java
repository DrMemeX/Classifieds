package ru.drmemex.classifieds.feature.comment.service;

import ru.drmemex.classifieds.feature.comment.dto.CommentRequest;
import ru.drmemex.classifieds.feature.comment.dto.CommentResponse;

import java.util.List;

public interface CommentService {

    CommentResponse create(
            Long advertisementId,
            CommentRequest request
    );

    List<CommentResponse> getByAdvertisementId(
            Long advertisementId
    );

    void delete(Long commentId);
}