package ru.drmemex.classifieds.feature.user.dto.auth.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeLoginUserRequest(

        @NotBlank
        @Size(min = 4, max = 50)
        String newLogin

) {
}
