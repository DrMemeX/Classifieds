package ru.drmemex.classifieds.feature.user.service;

import ru.drmemex.classifieds.feature.user.dto.account.UpdateUserProfileRequest;
import ru.drmemex.classifieds.feature.user.dto.account.CurrentUserResponse;
import ru.drmemex.classifieds.feature.user.dto.admin.AdminUserResponse;
import ru.drmemex.classifieds.feature.user.dto.auth.login.ChangeLoginUserRequest;
import ru.drmemex.classifieds.feature.user.dto.auth.login.LoginUserRequest;
import ru.drmemex.classifieds.feature.user.dto.auth.login.LoginUserResponse;
import ru.drmemex.classifieds.feature.user.dto.auth.password.ChangePasswordUserRequest;
import ru.drmemex.classifieds.feature.user.dto.auth.register.RegisterUserRequest;
import ru.drmemex.classifieds.feature.user.dto.auth.register.RegisterUserResponse;
import ru.drmemex.classifieds.feature.user.model.UserRole;
import ru.drmemex.classifieds.feature.user.model.UserStatus;

import java.util.List;

public interface UserService {

    RegisterUserResponse registerUser(RegisterUserRequest request);

    RegisterUserResponse registerAdmin(RegisterUserRequest request);

    LoginUserResponse login(LoginUserRequest request);

    void changeLogin(ChangeLoginUserRequest request);

    void changePassword(ChangePasswordUserRequest request);

    CurrentUserResponse getAccount();

    void updateProfile(UpdateUserProfileRequest request);

    void deleteAccount();

    List<AdminUserResponse> getUsers(
            UserStatus status,
            UserRole role
    );

    AdminUserResponse getUser(Long id);

    void blockUser(Long id);

    void unblockUser(Long id);
}
