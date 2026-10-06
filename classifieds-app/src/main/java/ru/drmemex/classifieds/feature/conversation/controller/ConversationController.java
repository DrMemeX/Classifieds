package ru.drmemex.classifieds.feature.conversation.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.common.util.pagination.dto.PageResponse;
import ru.drmemex.classifieds.feature.conversation.dto.ConversationResponse;
import ru.drmemex.classifieds.feature.conversation.service.ConversationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/conversations")
@PreAuthorize("hasRole('USER')")
@Validated
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
    public PageResponse<ConversationResponse> getMyConversations(
            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,
            @RequestParam(defaultValue = "20")
            @Min(1)
            @Max(100)
            int size
    ) {
        return conversationService.getMyConversations(
                new PageRequest(
                        page,
                        size
                )
        );
    }
}