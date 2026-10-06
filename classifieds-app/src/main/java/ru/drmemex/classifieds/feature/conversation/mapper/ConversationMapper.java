package ru.drmemex.classifieds.feature.conversation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.drmemex.classifieds.feature.conversation.dto.ConversationResponse;
import ru.drmemex.classifieds.feature.conversation.entity.Conversation;

@Mapper(componentModel = "spring")
public interface ConversationMapper {

    @Mapping(target = "advertisementId", source = "advertisement.id")
    @Mapping(target = "buyerId", source = "buyer.id")
    @Mapping(target = "sellerId", source = "advertisement.seller.id")
    ConversationResponse toResponse(Conversation conversation);
}
