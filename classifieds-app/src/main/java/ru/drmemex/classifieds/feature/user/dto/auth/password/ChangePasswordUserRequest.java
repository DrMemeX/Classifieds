package ru.drmemex.classifieds.feature.user.dto.auth.password;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordUserRequest(

        @NotBlank
        String currentPassword,

        @NotBlank
        @Size(min = 8, max = 64)
        String newPassword

) {
}
