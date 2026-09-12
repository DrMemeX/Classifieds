package ru.drmemex.classifieds.feature.comment.service;

import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.common.util.pagination.dto.PageResponse;
import ru.drmemex.classifieds.feature.comment.dto.CommentRequest;
import ru.drmemex.classifieds.feature.comment.dto.CommentResponse;

public interface CommentService {

    CommentResponse create(
            Long advertisementId,
            CommentRequest request
    );

    PageResponse<CommentResponse> getByAdvertisementId(
            Long advertisementId,
            PageRequest pageRequest
    );

    void delete(Long commentId);
}