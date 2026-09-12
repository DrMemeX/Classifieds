package ru.drmemex.classifieds.feature.conversation.message.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
import ru.drmemex.classifieds.feature.conversation.message.dto.MessageRequest;
import ru.drmemex.classifieds.feature.conversation.message.dto.MessageResponse;
import ru.drmemex.classifieds.feature.conversation.message.service.MessageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/conversations/{conversationId}/messages")
@PreAuthorize("hasRole('USER')")
@Validated
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse send(
            @PathVariable
            Long conversationId,
            @Valid
            @RequestBody
            MessageRequest request
    ) {
        return messageService.send(
                conversationId,
                request
        );
    }

    @GetMapping
    public PageResponse<MessageResponse> getByConversationId(
            @PathVariable
            Long conversationId,
            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,
            @RequestParam(defaultValue = "20")
            @Min(1)
            @Max(100)
            int size
    ) {
        return messageService.getByConversationId(
                conversationId,
                new PageRequest(
                        page,
                        size
                )
        );
    }

    @GetMapping("/search")
    public PageResponse<MessageResponse> searchByConversationAndText(
            @PathVariable
            Long conversationId,
            @RequestParam
            @NotBlank
            String text,
            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,
            @RequestParam(defaultValue = "20")
            @Min(1)
            @Max(100)
            int size
    ) {
        return messageService.searchByConversationAndText(
                conversationId,
                text,
                new PageRequest(
                        page,
                        size
                )
        );
    }
}