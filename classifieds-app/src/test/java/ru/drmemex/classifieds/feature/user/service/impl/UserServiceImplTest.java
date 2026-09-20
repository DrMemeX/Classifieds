package ru.drmemex.classifieds.feature.user.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.common.util.pagination.dto.PageResponse;
import ru.drmemex.classifieds.feature.advertisement.entity.Advertisement;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
import ru.drmemex.classifieds.feature.advertisement.repository.AdvertisementRepository;
import ru.drmemex.classifieds.feature.user.dto.account.CurrentUserResponse;
import ru.drmemex.classifieds.feature.user.dto.account.UpdateUserProfileRequest;
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
import ru.drmemex.classifieds.feature.user.exception.DeletedUserCannotBeBlockedException;
import ru.drmemex.classifieds.feature.user.exception.EmptyProfileUpdateException;
import ru.drmemex.classifieds.feature.user.exception.LoginAlreadyExistsException;
import ru.drmemex.classifieds.feature.user.exception.PhoneAlreadyExistsException;
import ru.drmemex.classifieds.feature.user.exception.SuperAdminCannotBeDeletedException;
import ru.drmemex.classifieds.feature.user.exception.UserAlreadyActiveException;
import ru.drmemex.classifieds.feature.user.exception.UserAlreadyBlockedException;
import ru.drmemex.classifieds.feature.user.exception.UserCannotBeBlockedException;
import ru.drmemex.classifieds.feature.user.exception.UserCannotBeUnblockedException;
import ru.drmemex.classifieds.feature.user.exception.UserNotFoundException;
import ru.drmemex.classifieds.feature.user.mapper.UserMapper;
import ru.drmemex.classifieds.feature.user.model.UserRole;
import ru.drmemex.classifieds.feature.user.model.UserStatus;
import ru.drmemex.classifieds.feature.user.repository.UserProfileRepository;
import ru.drmemex.classifieds.feature.user.repository.UserRepository;
import ru.drmemex.classifieds.security.exception.InvalidCredentialsException;
import ru.drmemex.classifieds.security.jwt.service.JwtService;
import ru.drmemex.classifieds.security.provider.CurrentUserProvider;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUser_ShouldRegisterUser() {

        RegisterUserRequest request =
                new RegisterUserRequest(
                        "andrei-user",
                        "Andrei1234",
                        "Andrei",
                        "Konovalov",
                        "+79991234567"
                );

        User user = new User();
        UserProfile profile = new UserProfile();

        RegisterUserResponse expectedResponse =
                mock(RegisterUserResponse.class);

        when(userRepository.existsByLogin("andrei-user"))
                .thenReturn(false);

        when(userProfileRepository.existsByPhone("+79991234567"))
                .thenReturn(false);

        when(userMapper.toUser(request))
                .thenReturn(user);

        when(userMapper.toUserProfile(request))
                .thenReturn(profile);

        when(passwordEncoder.encode("Andrei1234"))
                .thenReturn("encodedPassword");

        when(userMapper.toRegisterUserResponse(user))
                .thenReturn(expectedResponse);

        RegisterUserResponse result =
                userService.registerUser(request);

        assertEquals(expectedResponse, result);

        assertEquals(
                "encodedPassword",
                user.getPassword()
        );

        assertEquals(
                UserRole.USER,
                user.getRole()
        );

        assertEquals(
                UserStatus.ACTIVE,
                user.getStatus()
        );

        assertNotNull(user.getCreatedAt());

        assertSame(
                profile,
                user.getProfile()
        );

        assertSame(
                user,
                profile.getUser()
        );

        verify(userRepository)
                .save(user);
    }

    @Test
    void registerUser_ShouldThrowException_WhenLoginAlreadyExists() {

        RegisterUserRequest request =
                new RegisterUserRequest(
                        "andrei-user",
                        "Andrei1234",
                        "Andrei",
                        "Konovalov",
                        "+79991234567"
                );

        when(userRepository.existsByLogin("andrei-user"))
                .thenReturn(true);

        assertThrows(
                LoginAlreadyExistsException.class,
                () -> userService.registerUser(request)
        );

        verify(userProfileRepository, never())
                .existsByPhone(anyString());

        verify(userMapper, never())
                .toUser(any(RegisterUserRequest.class));

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenPhoneAlreadyExists() {

        RegisterUserRequest request =
                new RegisterUserRequest(
                        "andrei-user",
                        "Andrei1234",
                        "Andrei",
                        "Konovalov",
                        "+79991234567"
                );

        when(userRepository.existsByLogin("andrei-user"))
                .thenReturn(false);

        when(userProfileRepository.existsByPhone("+79991234567"))
                .thenReturn(true);

        assertThrows(
                PhoneAlreadyExistsException.class,
                () -> userService.registerUser(request)
        );

        verify(userMapper, never())
                .toUser(any(RegisterUserRequest.class));

        verify(userMapper, never())
                .toUserProfile(any(RegisterUserRequest.class));

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void registerAdmin_ShouldRegisterAdmin() {

        RegisterUserRequest request =
                new RegisterUserRequest(
                        "andrei-admin",
                        "Andrei1234",
                        "Andrei",
                        "Konovalov",
                        "+79991234568"
                );

        User user = new User();
        UserProfile profile = new UserProfile();

        RegisterUserResponse expectedResponse =
                mock(RegisterUserResponse.class);

        when(userRepository.existsByLogin("andrei-admin"))
                .thenReturn(false);

        when(userProfileRepository.existsByPhone("+79991234568"))
                .thenReturn(false);

        when(userMapper.toUser(request))
                .thenReturn(user);

        when(userMapper.toUserProfile(request))
                .thenReturn(profile);

        when(passwordEncoder.encode("Andrei1234"))
                .thenReturn("encodedPassword");

        when(userMapper.toRegisterUserResponse(user))
                .thenReturn(expectedResponse);

        RegisterUserResponse result =
                userService.registerAdmin(request);

        assertEquals(expectedResponse, result);

        assertEquals(
                "encodedPassword",
                user.getPassword()
        );

        assertEquals(
                UserRole.ADMIN,
                user.getRole()
        );

        assertEquals(
                UserStatus.ACTIVE,
                user.getStatus()
        );

        assertNotNull(user.getCreatedAt());

        assertSame(
                profile,
                user.getProfile()
        );

        assertSame(
                user,
                profile.getUser()
        );

        verify(userRepository)
                .save(user);
    }

    @Test
    void login_ShouldReturnLoginResponse() {

        LoginUserRequest request =
                new LoginUserRequest(
                        "andrei-user",
                        "Andrei1234"
                );

        User user = new User();
        user.setPassword("encodedPassword");

        UserProfile profile = new UserProfile();
        user.setProfile(profile);

        LoginUserResponse expectedResponse =
                mock(LoginUserResponse.class);

        when(userRepository.findByLoginAndStatus(
                "andrei-user",
                UserStatus.ACTIVE
        )).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "Andrei1234",
                "encodedPassword"
        )).thenReturn(true);

        when(jwtService.generateAccessToken(user))
                .thenReturn("accessToken");

        when(userMapper.toLoginUserResponse(
                user,
                profile,
                "accessToken"
        )).thenReturn(expectedResponse);

        LoginUserResponse result =
                userService.login(request);

        assertEquals(
                expectedResponse,
                result
        );

        verify(userRepository)
                .findByLoginAndStatus(
                        "andrei-user",
                        UserStatus.ACTIVE
                );

        verify(passwordEncoder)
                .matches(
                        "Andrei1234",
                        "encodedPassword"
                );

        verify(jwtService)
                .generateAccessToken(user);

        verify(userMapper)
                .toLoginUserResponse(
                        user,
                        profile,
                        "accessToken"
                );
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {

        LoginUserRequest request =
                new LoginUserRequest(
                        "andrei-user",
                        "Andrei1234"
                );

        when(userRepository.findByLoginAndStatus(
                "andrei-user",
                UserStatus.ACTIVE
        )).thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(request)
        );

        verify(passwordEncoder, never())
                .matches(
                        anyString(),
                        anyString()
                );

        verify(jwtService, never())
                .generateAccessToken(any(User.class));

        verify(userMapper, never())
                .toLoginUserResponse(
                        any(User.class),
                        any(UserProfile.class),
                        anyString()
                );
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsInvalid() {

        LoginUserRequest request =
                new LoginUserRequest(
                        "andrei-user",
                        "WrongPassword123"
                );

        User user = new User();
        user.setPassword("encodedPassword");

        when(userRepository.findByLoginAndStatus(
                "andrei-user",
                UserStatus.ACTIVE
        )).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "WrongPassword123",
                "encodedPassword"
        )).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.login(request)
        );

        verify(jwtService, never())
                .generateAccessToken(any(User.class));

        verify(userMapper, never())
                .toLoginUserResponse(
                        any(User.class),
                        any(UserProfile.class),
                        anyString()
                );
    }

    @Test
    void changeLogin_ShouldChangeLogin() {

        ChangeLoginUserRequest request =
                new ChangeLoginUserRequest(
                        "andrei-new"
                );

        User user = new User();

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user);

        when(userRepository.existsByLogin("andrei-new"))
                .thenReturn(false);

        userService.changeLogin(request);

        assertEquals(
                "andrei-new",
                user.getLogin()
        );

        assertNotNull(
                user.getUpdatedAt()
        );

        verify(userRepository)
                .update(user);
    }

    @Test
    void changeLogin_ShouldThrowException_WhenLoginAlreadyExists() {

        ChangeLoginUserRequest request =
                new ChangeLoginUserRequest(
                        "andrei-new"
                );

        User user = new User();
        user.setLogin("andrei-user");

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user);

        when(userRepository.existsByLogin("andrei-new"))
                .thenReturn(true);

        assertThrows(
                LoginAlreadyExistsException.class,
                () -> userService.changeLogin(request)
        );

        assertEquals(
                "andrei-user",
                user.getLogin()
        );

        verify(userRepository, never())
                .update(any(User.class));
    }

    @Test
    void changePassword_ShouldChangePassword() {

        ChangePasswordUserRequest request =
                new ChangePasswordUserRequest(
                        "Andrei1234",
                        "Andrei5678"
                );

        User user = new User();
        user.setPassword("encodedOldPassword");

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user);

        when(passwordEncoder.matches(
                "Andrei1234",
                "encodedOldPassword"
        )).thenReturn(true);

        when(passwordEncoder.encode("Andrei5678"))
                .thenReturn("encodedNewPassword");

        userService.changePassword(request);

        assertEquals(
                "encodedNewPassword",
                user.getPassword()
        );

        assertNotNull(
                user.getUpdatedAt()
        );

        verify(passwordEncoder)
                .encode("Andrei5678");

        verify(userRepository)
                .update(user);
    }

    @Test
    void changePassword_ShouldThrowException_WhenCurrentPasswordIsInvalid() {

        ChangePasswordUserRequest request =
                new ChangePasswordUserRequest(
                        "WrongPassword123",
                        "Andrei5678"
                );

        User user = new User();
        user.setPassword("encodedOldPassword");

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user);

        when(passwordEncoder.matches(
                "WrongPassword123",
                "encodedOldPassword"
        )).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> userService.changePassword(request)
        );

        assertEquals(
                "encodedOldPassword",
                user.getPassword()
        );

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .update(any(User.class));
    }

    @Test
    void getAccount_ShouldReturnCurrentUser() {

        User user = new User();

        UserProfile profile = new UserProfile();

        user.setProfile(profile);

        CurrentUserResponse expectedResponse =
                mock(CurrentUserResponse.class);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user);

        when(userMapper.toCurrentUserResponse(user, profile))
                .thenReturn(expectedResponse);

        CurrentUserResponse result =
                userService.getAccount();

        assertEquals(expectedResponse, result);

        verify(currentUserProvider)
                .getCurrentUser();

        verify(userMapper)
                .toCurrentUserResponse(user, profile);
    }

    @Test
    void updateProfile_ShouldUpdateProfile() {

        UpdateUserProfileRequest request =
                new UpdateUserProfileRequest(
                        "Andrew",
                        "Smith",
                        "+79991234568"
                );

        User user = new User();

        UserProfile profile = new UserProfile();
        profile.setFirstName("Andrei");
        profile.setLastName("Konovalov");
        profile.setPhone("+79991234567");

        user.setProfile(profile);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user);

        when(userProfileRepository.existsByPhone("+79991234568"))
                .thenReturn(false);

        userService.updateProfile(request);

        assertEquals(
                "Andrew",
                profile.getFirstName()
        );

        assertEquals(
                "Smith",
                profile.getLastName()
        );

        assertEquals(
                "+79991234568",
                profile.getPhone()
        );

        assertNotNull(
                user.getUpdatedAt()
        );

        verify(userProfileRepository)
                .existsByPhone("+79991234568");

        verify(userProfileRepository)
                .update(profile);

        verify(userRepository)
                .update(user);
    }

    @Test
    void updateProfile_ShouldUpdateOnlyProvidedFields() {

        UpdateUserProfileRequest request =
                new UpdateUserProfileRequest(
                        "Andrew",
                        null,
                        null
                );

        User user = new User();

        UserProfile profile = new UserProfile();
        profile.setFirstName("Andrei");
        profile.setLastName("Konovalov");
        profile.setPhone("+79991234567");

        user.setProfile(profile);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user);

        userService.updateProfile(request);

        assertEquals(
                "Andrew",
                profile.getFirstName()
        );

        assertEquals(
                "Konovalov",
                profile.getLastName()
        );

        assertEquals(
                "+79991234567",
                profile.getPhone()
        );

        assertNotNull(
                user.getUpdatedAt()
        );

        verify(userProfileRepository, never())
                .existsByPhone(anyString());

        verify(userProfileRepository)
                .update(profile);

        verify(userRepository)
                .update(user);
    }

    @Test
    void updateProfile_ShouldThrowException_WhenRequestIsEmpty() {

        UpdateUserProfileRequest request =
                new UpdateUserProfileRequest(
                        null,
                        null,
                        null
                );

        assertThrows(
                EmptyProfileUpdateException.class,
                () -> userService.updateProfile(request)
        );

        verify(currentUserProvider, never())
                .getCurrentUser();

        verify(userProfileRepository, never())
                .update(any(UserProfile.class));

        verify(userRepository, never())
                .update(any(User.class));
    }

    @Test
    void updateProfile_ShouldThrowException_WhenPhoneAlreadyExists() {

        UpdateUserProfileRequest request =
                new UpdateUserProfileRequest(
                        null,
                        null,
                        "+79991234568"
                );

        User user = new User();

        UserProfile profile = new UserProfile();
        profile.setPhone("+79991234567");

        user.setProfile(profile);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user);

        when(userProfileRepository.existsByPhone("+79991234568"))
                .thenReturn(true);

        assertThrows(
                PhoneAlreadyExistsException.class,
                () -> userService.updateProfile(request)
        );

        assertEquals(
                "+79991234567",
                profile.getPhone()
        );

        verify(userProfileRepository, never())
                .update(any(UserProfile.class));

        verify(userRepository, never())
                .update(any(User.class));
    }

    @Test
    void updateProfile_ShouldUpdateProfile_WhenPhoneIsUnchanged() {

        UpdateUserProfileRequest request =
                new UpdateUserProfileRequest(
                        null,
                        null,
                        "+79991234567"
                );

        User user = new User();

        UserProfile profile = new UserProfile();
        profile.setPhone("+79991234567");

        user.setProfile(profile);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user);

        userService.updateProfile(request);

        assertEquals(
                "+79991234567",
                profile.getPhone()
        );

        assertNotNull(
                user.getUpdatedAt()
        );

        verify(userProfileRepository, never())
                .existsByPhone(anyString());

        verify(userProfileRepository)
                .update(profile);

        verify(userRepository)
                .update(user);
    }

    @Test
    void deleteAccount_ShouldDeleteAccount() {

        User user = new User();
        user.setId(1L);
        user.setLogin("andrei-user");
        user.setPassword("encodedPassword");
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);

        UserProfile profile = new UserProfile();
        profile.setFirstName("Andrei");
        profile.setLastName("Konovalov");
        profile.setPhone("+79991234567");

        user.setProfile(profile);

        Advertisement advertisement = new Advertisement();
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user);

        when(passwordEncoder.encode(anyString()))
                .thenReturn("deletedPassword");

        when(advertisementRepository.findBySellerId(
                1L,
                AdvertisementStatus.ACTIVE
        )).thenReturn(List.of(advertisement));

        userService.deleteAccount();

        assertEquals(
                UserStatus.DELETED,
                user.getStatus()
        );

        assertEquals(
                "deleted_1",
                user.getLogin()
        );

        assertEquals(
                "deletedPassword",
                user.getPassword()
        );

        assertNotNull(user.getUpdatedAt());

        assertEquals(
                "Удалённый",
                profile.getFirstName()
        );

        assertEquals(
                "пользователь",
                profile.getLastName()
        );

        assertNull(
                profile.getPhone()
        );

        assertEquals(
                AdvertisementStatus.INACTIVE,
                advertisement.getAdvertisementStatus()
        );

        assertNotNull(
                advertisement.getUpdatedAt()
        );

        verify(passwordEncoder)
                .encode(anyString());

        verify(userProfileRepository)
                .update(profile);

        verify(userRepository)
                .update(user);

        verify(advertisementRepository)
                .findBySellerId(
                        1L,
                        AdvertisementStatus.ACTIVE
                );

        verify(advertisementRepository)
                .update(advertisement);
    }

    @Test
    void deleteAccount_ShouldThrowException_WhenUserIsSuperAdmin() {

        User user = new User();
        user.setRole(UserRole.SUPER_ADMIN);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(user);

        assertThrows(
                SuperAdminCannotBeDeletedException.class,
                () -> userService.deleteAccount()
        );

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userProfileRepository, never())
                .update(any(UserProfile.class));

        verify(userRepository, never())
                .update(any(User.class));
    }

    @Test
    void getUsers_ShouldReturnPageOfUsers() {

        PageRequest pageRequest =
                new PageRequest(
                        1,
                        2
                );

        User user1 = new User();
        UserProfile profile1 = new UserProfile();
        user1.setProfile(profile1);

        User user2 = new User();
        UserProfile profile2 = new UserProfile();
        user2.setProfile(profile2);

        AdminUserResponse response1 =
                mock(AdminUserResponse.class);

        AdminUserResponse response2 =
                mock(AdminUserResponse.class);

        when(userRepository.findByFilters(
                UserStatus.ACTIVE,
                UserRole.USER,
                pageRequest
        )).thenReturn(List.of(user1, user2));

        when(userMapper.toAdminUserResponse(
                user1,
                profile1
        )).thenReturn(response1);

        when(userMapper.toAdminUserResponse(
                user2,
                profile2
        )).thenReturn(response2);

        when(userRepository.countByFilters(
                UserStatus.ACTIVE,
                UserRole.USER
        )).thenReturn(5L);

        PageResponse<AdminUserResponse> result =
                userService.getUsers(
                        UserStatus.ACTIVE,
                        UserRole.USER,
                        pageRequest
                );

        assertEquals(
                List.of(response1, response2),
                result.content()
        );

        assertEquals(
                1,
                result.page()
        );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                5L,
                result.totalElements()
        );

        assertEquals(
                3,
                result.totalPages()
        );

        verify(userRepository)
                .findByFilters(
                        UserStatus.ACTIVE,
                        UserRole.USER,
                        pageRequest
                );

        verify(userMapper)
                .toAdminUserResponse(
                        user1,
                        profile1
                );

        verify(userMapper)
                .toAdminUserResponse(
                        user2,
                        profile2
                );

        verify(userRepository)
                .countByFilters(
                        UserStatus.ACTIVE,
                        UserRole.USER
                );
    }

    @Test
    void getUser_ShouldReturnUser() {

        User user = new User();

        UserProfile profile = new UserProfile();
        user.setProfile(profile);

        AdminUserResponse expectedResponse =
                mock(AdminUserResponse.class);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userMapper.toAdminUserResponse(
                user,
                profile
        )).thenReturn(expectedResponse);

        AdminUserResponse result =
                userService.getUser(1L);

        assertEquals(
                expectedResponse,
                result
        );

        verify(userRepository)
                .findById(1L);

        verify(userMapper)
                .toAdminUserResponse(
                        user,
                        profile
                );
    }

    @Test
    void getUser_ShouldThrowException_WhenUserNotFound() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.getUser(1L)
        );

        verify(userMapper, never())
                .toAdminUserResponse(
                        any(User.class),
                        any(UserProfile.class)
                );
    }

    @Test
    void blockUser_ShouldBlockUser() {

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.ADMIN);

        User user = new User();
        user.setId(2L);
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);

        Advertisement advertisement = new Advertisement();
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        when(advertisementRepository.findBySellerId(
                2L,
                AdvertisementStatus.ACTIVE
        )).thenReturn(List.of(advertisement));

        userService.blockUser(2L);

        assertEquals(
                UserStatus.BLOCKED,
                user.getStatus()
        );

        assertNotNull(
                user.getBlockedAt()
        );

        assertEquals(
                AdvertisementStatus.INACTIVE,
                advertisement.getAdvertisementStatus()
        );

        assertNotNull(
                advertisement.getUpdatedAt()
        );

        verify(userRepository)
                .update(user);

        verify(advertisementRepository)
                .findBySellerId(
                        2L,
                        AdvertisementStatus.ACTIVE
                );

        verify(advertisementRepository)
                .update(advertisement);
    }

    @Test
    void blockUser_ShouldThrowException_WhenAdminBlocksSelf() {

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.ADMIN);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        assertThrows(
                AdminCannotBlockSelfException.class,
                () -> userService.blockUser(1L)
        );

        verify(userRepository, never())
                .findById(1L);

        verify(userRepository, never())
                .update(any(User.class));
    }

    @Test
    void blockUser_ShouldThrowException_WhenUserNotFound() {

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.ADMIN);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(userRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.blockUser(2L)
        );

        verify(userRepository, never())
                .update(any(User.class));
    }

    @Test
    void blockUser_ShouldThrowException_WhenTargetIsSuperAdmin() {

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.ADMIN);

        User user = new User();
        user.setRole(UserRole.SUPER_ADMIN);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        assertThrows(
                UserCannotBeBlockedException.class,
                () -> userService.blockUser(2L)
        );

        verify(userRepository, never())
                .update(any(User.class));
    }

    @Test
    void blockUser_ShouldThrowException_WhenAdminBlocksAdmin() {

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.ADMIN);

        User user = new User();
        user.setRole(UserRole.ADMIN);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        assertThrows(
                UserCannotBeBlockedException.class,
                () -> userService.blockUser(2L)
        );

        verify(userRepository, never())
                .update(any(User.class));
    }

    @Test
    void blockUser_ShouldThrowException_WhenUserIsDeleted() {

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.ADMIN);

        User user = new User();
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.DELETED);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        assertThrows(
                DeletedUserCannotBeBlockedException.class,
                () -> userService.blockUser(2L)
        );

        verify(userRepository, never())
                .update(any(User.class));
    }

    @Test
    void blockUser_ShouldThrowException_WhenUserAlreadyBlocked() {

        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setRole(UserRole.ADMIN);

        User user = new User();
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.BLOCKED);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        assertThrows(
                UserAlreadyBlockedException.class,
                () -> userService.blockUser(2L)
        );

        verify(userRepository, never())
                .update(any(User.class));
    }

    @Test
    void unblockUser_ShouldUnblockUser() {

        User currentUser = new User();
        currentUser.setRole(UserRole.ADMIN);

        User user = new User();
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.BLOCKED);
        user.setBlockedAt(OffsetDateTime.now());

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        userService.unblockUser(2L);

        assertEquals(
                UserStatus.ACTIVE,
                user.getStatus()
        );

        assertNull(
                user.getBlockedAt()
        );

        verify(userRepository)
                .update(user);
    }

    @Test
    void unblockUser_ShouldThrowException_WhenUserNotFound() {

        User currentUser = new User();

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(userRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.unblockUser(2L)
        );

        verify(userRepository, never())
                .update(any(User.class));
    }

    @Test
    void unblockUser_ShouldThrowException_WhenTargetIsSuperAdmin() {

        User currentUser = new User();
        currentUser.setRole(UserRole.ADMIN);

        User user = new User();
        user.setRole(UserRole.SUPER_ADMIN);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        assertThrows(
                UserCannotBeUnblockedException.class,
                () -> userService.unblockUser(2L)
        );

        verify(userRepository, never())
                .update(any(User.class));
    }

    @Test
    void unblockUser_ShouldThrowException_WhenAdminUnblocksAdmin() {

        User currentUser = new User();
        currentUser.setRole(UserRole.ADMIN);

        User user = new User();
        user.setRole(UserRole.ADMIN);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        assertThrows(
                UserCannotBeUnblockedException.class,
                () -> userService.unblockUser(2L)
        );

        verify(userRepository, never())
                .update(any(User.class));
    }

    @Test
    void unblockUser_ShouldThrowException_WhenUserAlreadyActive() {

        User currentUser = new User();
        currentUser.setRole(UserRole.ADMIN);

        User user = new User();
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        assertThrows(
                UserAlreadyActiveException.class,
                () -> userService.unblockUser(2L)
        );

        verify(userRepository, never())
                .update(any(User.class));
    }

    @Test
    void unblockUser_ShouldThrowException_WhenUserIsDeleted() {

        User currentUser = new User();
        currentUser.setRole(UserRole.ADMIN);

        User user = new User();
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.DELETED);

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(userRepository.findById(2L))
                .thenReturn(Optional.of(user));

        assertThrows(
                DeletedUserCannotBeActivatedException.class,
                () -> userService.unblockUser(2L)
        );

        verify(userRepository, never())
                .update(any(User.class));
    }
}