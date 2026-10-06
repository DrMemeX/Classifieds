package ru.drmemex.classifieds.feature.user.dto.account;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(

        @Pattern(
                regexp = ".*\\S.*",
                message = "First name must not be blank"
        )
        @Size(
                max = 100,
                message = "First name cannot exceed 100 characters"
        )
        String firstName,

        @Pattern(
                regexp = ".*\\S.*",
                message = "Last name must not be blank"
        )
        @Size(
                max = 100,
                message = "Last name cannot exceed 100 characters"
        )
        String lastName,

        @Pattern(
                regexp = ".*\\S.*",
                message = "Phone must not be blank"
        )
        @Size(
                max = 20,
                message = "Phone cannot exceed 20 characters"
        )
        String phone
) {
}
