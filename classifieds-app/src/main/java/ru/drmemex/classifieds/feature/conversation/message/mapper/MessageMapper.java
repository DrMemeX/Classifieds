package ru.drmemex.classifieds.feature.conversation.message.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.drmemex.classifieds.feature.conversation.message.dto.MessageResponse;
import ru.drmemex.classifieds.feature.conversation.message.entity.Message;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "conversationId", source = "conversation.id")
    @Mapping(target = "authorId", source = "author.id")
    MessageResponse toResponse(Message message);
}
