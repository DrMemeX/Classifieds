package ru.drmemex.classifieds.feature.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drmemex.classifieds.feature.user.dto.account.CurrentUserResponse;
import ru.drmemex.classifieds.feature.user.dto.account.UpdateUserProfileRequest;
import ru.drmemex.classifieds.feature.user.dto.auth.login.ChangeLoginUserRequest;
import ru.drmemex.classifieds.feature.user.dto.auth.login.LoginUserRequest;
import ru.drmemex.classifieds.feature.user.dto.auth.login.LoginUserResponse;
import ru.drmemex.classifieds.feature.user.dto.auth.password.ChangePasswordUserRequest;
import ru.drmemex.classifieds.feature.user.dto.auth.register.RegisterUserRequest;
import ru.drmemex.classifieds.feature.user.dto.auth.register.RegisterUserResponse;
import ru.drmemex.classifieds.feature.user.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterUserResponse register(
            @Valid
            @RequestBody
            RegisterUserRequest request
    ) {
        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public LoginUserResponse login(
            @Valid
            @RequestBody
            LoginUserRequest request
    ) {
        return userService.login(request);
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> getAccount() {
        return ResponseEntity.ok(userService.getAccount());
    }

    @PatchMapping("/me/profile")
    public ResponseEntity<Void> updateProfile(
            @Valid
            @RequestBody
            UpdateUserProfileRequest request
    ) {
        userService.updateProfile(request);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/login")
    public ResponseEntity<Void> changeLogin(
            @Valid
            @RequestBody
            ChangeLoginUserRequest request
    ) {
        userService.changeLogin(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @Valid
            @RequestBody
            ChangePasswordUserRequest request
    ) {
        userService.changePassword(request);
        return ResponseEntity.noContent().build();
    }

    //при удалении доделать удаление сопряженных объявлений и т.д.
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount() {
        userService.deleteAccount();

        return ResponseEntity.noContent().build();
    }
}
