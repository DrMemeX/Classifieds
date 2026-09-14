package ru.drmemex.classifieds.feature.user.dto.auth.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeLoginUserRequest(

        @NotBlank(message = "Login cannot be blank")
        @Size(
                min = 4,
                max = 50,
                message = "Login must contain from 4 to 50 characters"
        )
        String newLogin

) {
}