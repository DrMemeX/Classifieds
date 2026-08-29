package ru.drmemex.classifieds.feature.user.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateAdminUserRequest(

        @NotBlank(message = "Login cannot be blank")
        @Size(
                min = 4,
                max = 50,
                message = "Login must contain from 4 to 50 characters"
        )
                String login,

        @NotBlank(message = "First name cannot be blank")
        @Size(
                max = 100,
                message = "First name cannot exceed 100 characters"
        )
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        @Size(
                max = 100,
                message = "Last name cannot exceed 100 characters"
        )
        String lastName,

        @NotBlank(message = "Phone cannot be blank")
        @Pattern(
                regexp = "^\\+?[0-9]{10,15}$",
                message = "Phone number has invalid format"
        )
        String phone
) {
}
