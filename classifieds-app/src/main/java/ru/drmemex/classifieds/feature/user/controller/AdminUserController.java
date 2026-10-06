package ru.drmemex.classifieds.feature.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.common.util.pagination.dto.PageResponse;
import ru.drmemex.classifieds.feature.user.dto.admin.AdminUserResponse;
import ru.drmemex.classifieds.feature.user.dto.auth.register.RegisterUserRequest;
import ru.drmemex.classifieds.feature.user.dto.auth.register.RegisterUserResponse;
import ru.drmemex.classifieds.feature.user.model.UserRole;
import ru.drmemex.classifieds.feature.user.model.UserStatus;
import ru.drmemex.classifieds.feature.user.service.UserService;

@Validated
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterUserResponse registerAdmin(
            @Valid
            @RequestBody
            RegisterUserRequest request
    ) {
        return userService.registerAdmin(request);
    }

    @GetMapping
    public PageResponse<AdminUserResponse> getUsers(
            @RequestParam(required = false)
            UserStatus status,

            @RequestParam(required = false)
            UserRole role,

            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "20")
            @Min(1)
            @Max(100)
            int size
    ) {
        return userService.getUsers(
                status,
                role,
                new PageRequest(
                        page,
                        size
                )
        );
    }

    @GetMapping("/{id}")
    public AdminUserResponse getUser(
            @PathVariable
            Long id
    ) {
        return userService.getUser(id);
    }

    @PatchMapping("/{id}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void blockUser(
            @PathVariable
            Long id
    ) {
        userService.blockUser(id);
    }

    @PatchMapping("/{id}/unblock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unblockUser(
            @PathVariable
            Long id
    ) {
        userService.unblockUser(id);
    }
}
