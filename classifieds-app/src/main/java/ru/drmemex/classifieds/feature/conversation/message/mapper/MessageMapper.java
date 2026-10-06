package ru.drmemex.classifieds.feature.conversation.message.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.drmemex.classifieds.feature.conversation.message.dto.MessageResponse;
import ru.drmemex.classifieds.feature.conversation.message.entity.Message;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "conversationId", source = "message.conversation.id")
    @Mapping(target = "authorId", source = "message.author.id")
    @Mapping(target = "text", source = "text")
    MessageResponse toResponse(
            Message message,
            String text
    );
}
