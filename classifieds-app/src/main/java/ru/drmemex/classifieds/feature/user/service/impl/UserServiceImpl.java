package ru.drmemex.classifieds.feature.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.drmemex.classifieds.feature.user.dto.account.UpdateUserProfileRequest;
import ru.drmemex.classifieds.feature.user.dto.account.CurrentUserResponse;
import ru.drmemex.classifieds.feature.user.dto.admin.AdminUserResponse;
import ru.drmemex.classifieds.feature.user.dto.auth.login.ChangeLoginUserRequest;
import ru.drmemex.classifieds.feature.user.dto.auth.login.LoginUserRequest;
import ru.drmemex.classifieds.feature.user.dto.auth.login.LoginUserResponse;
import ru.drmemex.classifieds.feature.user.dto.auth.password.ChangePasswordUserRequest;
import ru.drmemex.classifieds.feature.user.dto.auth.register.RegisterUserRequest;
import ru.drmemex.classifieds.feature.user.dto.auth.register.RegisterUserResponse;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.feature.user.entity.UserProfile;
import ru.drmemex.classifieds.feature.user.exception.AdminCannotBlockSelfException;
import ru.drmemex.classifieds.feature.user.exception.DeletedUserCannotBeActivatedException;
import ru.drmemex.classifieds.feature.user.exception.EmptyProfileUpdateException;
import ru.drmemex.classifieds.feature.user.exception.UserAlreadyActiveException;
import ru.drmemex.classifieds.feature.user.exception.UserAlreadyBlockedException;
import ru.drmemex.classifieds.feature.user.exception.UserNotFoundException;
import ru.drmemex.classifieds.security.exception.InvalidCredentialsException;
import ru.drmemex.classifieds.feature.user.exception.LoginAlreadyExistsException;
import ru.drmemex.classifieds.feature.user.exception.PhoneAlreadyExistsException;
import ru.drmemex.classifieds.feature.user.mapper.UserMapper;
import ru.drmemex.classifieds.feature.user.model.UserRole;
import ru.drmemex.classifieds.feature.user.model.UserStatus;
import ru.drmemex.classifieds.feature.user.repository.UserProfileRepository;
import ru.drmemex.classifieds.feature.user.repository.UserRepository;
import ru.drmemex.classifieds.feature.user.service.UserService;
import ru.drmemex.classifieds.security.jwt.service.JwtService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserProfileRepository userProfileRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final UserMapper userMapper;


    @Override
    public RegisterUserResponse registerUser(RegisterUserRequest request) {

        User user = createUser(
                request,
                UserRole.USER
        );

        return userMapper.toRegisterUserResponse(user);
    }

    @Override
    public RegisterUserResponse registerAdmin(RegisterUserRequest request) {

        User user = createUser(
                request,
                UserRole.ADMIN
        );

        return userMapper.toRegisterUserResponse(user);
    }

    @Override
    public LoginUserResponse login(LoginUserRequest request) {

        User user = userRepository.findByLoginAndStatus(
                request.login(),
                UserStatus.ACTIVE
        ).orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtService.generateAccessToken(user);

        return userMapper.toLoginUserResponse(
                user,
                user.getProfile(),
                accessToken
        );
    }

    @Override
    public void changeLogin(ChangeLoginUserRequest request) {

        User user = getCurrentUser();

        if (userRepository.existsByLogin(request.newLogin())) {
            throw new LoginAlreadyExistsException();
        }

        user.setLogin(request.newLogin());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.update(user);
    }

    @Override
    public void changePassword(ChangePasswordUserRequest request) {

        User user = getCurrentUser();

        if (!passwordEncoder.matches(
                request.currentPassword(),
                user.getPassword()
        )) {
            throw new InvalidCredentialsException();
        }

        user.setPassword(
                passwordEncoder.encode(request.newPassword())
        );

        user.setUpdatedAt(LocalDateTime.now());

        userRepository.update(user);
    }

    @Override
    public CurrentUserResponse getAccount() {

        User user = getCurrentUser();

        return userMapper.toCurrentUserResponse(
                user,
                user.getProfile()
        );
    }

    @Override
    public void updateProfile(UpdateUserProfileRequest request) {

        if (request.firstName() == null
                && request.lastName() == null
                && request.phone() == null) {
            throw new EmptyProfileUpdateException();
        }

        User user = getCurrentUser();

        UserProfile profile = user.getProfile();

        if (request.firstName() != null) {
            profile.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            profile.setLastName(request.lastName());
        }

        if (request.phone() != null) {

            if (!profile.getPhone().equals(request.phone())
                    && userProfileRepository.existsByPhone(request.phone())) {
                throw new PhoneAlreadyExistsException();
            }

            profile.setPhone(request.phone());
        }

        user.setUpdatedAt(LocalDateTime.now());

        userProfileRepository.save(profile);
        userRepository.update(user);
    }

    @Override
    public void deleteAccount() {

        User user = getCurrentUser();

        user.setStatus(UserStatus.DELETED);
        user.setLogin("deleted_" + user.getId());
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

        user.setProfile(null);

        userRepository.update(user);

        //дописать логику с объявлениями
    }

    @Override
    public List<AdminUserResponse> getUsers(
            UserStatus status,
            UserRole role
    ) {
        List<User> users = userRepository.findByFilters(status, role);

        return users.stream()
                .map(user -> userMapper.toAdminUserResponse(
                        user,
                        user.getProfile()
                ))
                .toList();
    }

    @Override
    public AdminUserResponse getUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        return userMapper.toAdminUserResponse(
                user,
                user.getProfile()
        );
    }

    @Override
    public void blockUser(Long id) {

        User currentUser = getCurrentUser();

        if (currentUser.getId().equals(id)) {
            throw new AdminCannotBlockSelfException();
        }

        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new UserAlreadyBlockedException();
        }

        user.setStatus(UserStatus.BLOCKED);
        user.setBlockedAt(LocalDateTime.now());

        userRepository.update(user);

        //дописать логику с объявлениями
    }

    @Override
    public void unblockUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (user.getStatus() == UserStatus.ACTIVE) {
            throw new UserAlreadyActiveException();
        }

        if (user.getStatus() == UserStatus.DELETED) {
            throw new DeletedUserCannotBeActivatedException();
        }

        user.setStatus(UserStatus.ACTIVE);
        user.setBlockedAt(null);

        userRepository.update(user);
    }

    private User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findByLoginAndStatus(
                authentication.getName(),
                UserStatus.ACTIVE
        ).orElseThrow(InvalidCredentialsException::new);
    }

    private User createUser(
            RegisterUserRequest request,
            UserRole role
    ) {
        if (userRepository.existsByLogin(request.login())) {
            throw new LoginAlreadyExistsException();
        }

        if (userProfileRepository.existsByPhone(request.phone())) {
            throw new PhoneAlreadyExistsException();
        }

        User user = userMapper.toUser(request);
        UserProfile profile = userMapper.toUserProfile(request);

        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());

        user.setProfile(profile);
        profile.setUser(user);

        userRepository.save(user);

        return user;
    }

}
