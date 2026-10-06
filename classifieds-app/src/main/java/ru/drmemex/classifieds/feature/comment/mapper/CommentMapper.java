package ru.drmemex.classifieds.feature.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.drmemex.classifieds.feature.comment.dto.CommentResponse;
import ru.drmemex.classifieds.feature.comment.entity.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "advertisementId", source = "advertisement.id")
    @Mapping(target = "authorId", source = "author.id")
    CommentResponse toResponse(Comment comment);
}