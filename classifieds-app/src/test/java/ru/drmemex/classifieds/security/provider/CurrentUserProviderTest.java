package ru.drmemex.classifieds.security.provider;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.feature.user.model.UserStatus;
import ru.drmemex.classifieds.feature.user.repository.UserRepository;
import ru.drmemex.classifieds.security.exception.InvalidCredentialsException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentUserProviderTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CurrentUserProvider currentUserProvider;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUser_ShouldReturnActiveUser() {

        Authentication authentication =
                mock(Authentication.class);

        User user = mock(User.class);

        when(authentication.getName())
                .thenReturn("user");

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        when(userRepository.findByLoginAndStatus(
                "user",
                UserStatus.ACTIVE
        )).thenReturn(Optional.of(user));

        User result =
                currentUserProvider.getCurrentUser();

        assertSame(
                user,
                result
        );

        verify(userRepository)
                .findByLoginAndStatus(
                        "user",
                        UserStatus.ACTIVE
                );
    }

    @Test
    void getCurrentUser_ShouldThrowException_WhenActiveUserNotFound() {

        Authentication authentication =
                mock(Authentication.class);

        when(authentication.getName())
                .thenReturn("user");

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        when(userRepository.findByLoginAndStatus(
                "user",
                UserStatus.ACTIVE
        )).thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> currentUserProvider.getCurrentUser()
        );
    }
}