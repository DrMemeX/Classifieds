package ru.drmemex.classifieds.feature.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.drmemex.classifieds.feature.user.dto.account.CurrentUserResponse;
import ru.drmemex.classifieds.feature.user.dto.admin.AdminUserResponse;
import ru.drmemex.classifieds.feature.user.dto.auth.login.LoginUserResponse;
import ru.drmemex.classifieds.feature.user.dto.auth.register.RegisterUserRequest;
import ru.drmemex.classifieds.feature.user.dto.auth.register.RegisterUserResponse;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.feature.user.entity.UserProfile;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "blockedAt", ignore = true)
    @Mapping(target = "profile", ignore = true)
    User toUser(RegisterUserRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    UserProfile toUserProfile(RegisterUserRequest request);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "login", source = "user.login")
    @Mapping(target = "firstName", source = "profile.firstName")
    @Mapping(target = "lastName", source = "profile.lastName")
    @Mapping(target = "phone", source = "profile.phone")
    @Mapping(target = "role", source = "user.role")
    RegisterUserResponse toRegisterUserResponse(User user);

    @Mapping(target = "accessToken", source = "accessToken")
    @Mapping(target = "tokenType", constant = "Bearer")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "login", source = "user.login")
    @Mapping(target = "firstName", source = "profile.firstName")
    @Mapping(target = "lastName", source = "profile.lastName")
    @Mapping(target = "role", source = "user.role")
    LoginUserResponse toLoginUserResponse(
            User user,
            UserProfile profile,
            String accessToken
    );

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "login", source = "user.login")
    @Mapping(target = "role", source = "user.role")
    @Mapping(target = "status", source = "user.status")
    @Mapping(target = "firstName", source = "profile.firstName")
    @Mapping(target = "lastName", source = "profile.lastName")
    @Mapping(target = "phone", source = "profile.phone")
    CurrentUserResponse toCurrentUserResponse(User user, UserProfile profile);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "login", source = "user.login")
    @Mapping(target = "role", source = "user.role")
    @Mapping(target = "status", source = "user.status")
    @Mapping(target = "firstName", source = "profile.firstName")
    @Mapping(target = "lastName", source = "profile.lastName")
    @Mapping(target = "phone", source = "profile.phone")
    @Mapping(target = "createdAt", source = "user.createdAt")
    @Mapping(target = "updatedAt", source = "user.updatedAt")
    @Mapping(target = "blockedAt", source = "user.blockedAt")
    AdminUserResponse toAdminUserResponse(
            User user,
            UserProfile profile
    );
}