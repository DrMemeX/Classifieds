package ru.drmemex.classifieds.feature.conversation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drmemex.classifieds.feature.conversation.dto.ConversationResponse;
import ru.drmemex.classifieds.feature.conversation.service.ConversationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/conversations")
@PreAuthorize("hasRole('USER')")
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping("/advertisements/{advertisementId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationResponse create(
            @PathVariable
            Long advertisementId
    ) {
        return conversationService.create(advertisementId);
    }

    @GetMapping("/{conversationId}")
    public ConversationResponse getById(
            @PathVariable
            Long conversationId
    ) {
        return conversationService.getById(conversationId);
    }

    @GetMapping
    public List<ConversationResponse> getMyConversations() {
        return conversationService.getMyConversations();
    }
}
