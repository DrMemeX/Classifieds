package ru.drmemex.classifieds.feature.user.dto.auth.login;

import jakarta.validation.constraints.NotBlank;

public record LoginUserRequest(

    @NotBlank(message = "Login cannot be blank")
    String login,

    @NotBlank(message = "Password cannot be blank")
    String password

) {
}
