package ru.drmemex.classifieds.feature.user.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.feature.user.entity.UserProfile;
import ru.drmemex.classifieds.feature.user.model.UserRole;
import ru.drmemex.classifieds.feature.user.model.UserStatus;
import ru.drmemex.classifieds.feature.user.repository.UserProfileRepository;
import ru.drmemex.classifieds.integration.AbstractIntegrationTest;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserProfileRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void save_ShouldSaveUserProfile() {

        User user = createUser(
                "profile-save-test"
        );

        UserProfile profile = buildProfile(
                user,
                "Андрей",
                "Андреев",
                "+7999990000"
        );

        UserProfile savedProfile =
                userProfileRepository.save(profile);

        entityManager.flush();
        entityManager.clear();

        UserProfile actualProfile = entityManager.find(
                UserProfile.class,
                savedProfile.getId()
        );

        assertNotNull(
                actualProfile
        );

        assertEquals(
                user.getId(),
                actualProfile.getId()
        );

        assertEquals(
                "Андрей",
                actualProfile.getFirstName()
        );

        assertEquals(
                "Андреев",
                actualProfile.getLastName()
        );

        assertEquals(
                "+7999990000",
                actualProfile.getPhone()
        );
    }

    @Test
    void update_ShouldUpdateUserProfile() {

        User user = createUser(
                "profile-update-test"
        );

        UserProfile profile = createProfile(
                user,
                "Андрей",
                "Андреев",
                "+7999990000"
        );

        entityManager.flush();
        entityManager.clear();

        profile.setFirstName("Иван");
        profile.setLastName("Иванов");
        profile.setPhone("+7999990002");

        UserProfile updatedProfile =
                userProfileRepository.update(profile);

        entityManager.flush();
        entityManager.clear();

        UserProfile actualProfile = entityManager.find(
                UserProfile.class,
                updatedProfile.getId()
        );

        assertNotNull(
                actualProfile
        );

        assertEquals(
                "Иван",
                actualProfile.getFirstName()
        );

        assertEquals(
                "Иванов",
                actualProfile.getLastName()
        );

        assertEquals(
                "+7999990002",
                actualProfile.getPhone()
        );
    }

    @Test
    void findById_ShouldReturnUserProfile() {

        User user = createUser(
                "profile-find-test"
        );

        UserProfile profile = createProfile(
                user,
                "Андрей",
                "Андреев",
                "+7999990003"
        );

        entityManager.flush();

        Long profileId =
                profile.getId();

        entityManager.clear();

        Optional<UserProfile> result =
                userProfileRepository.findById(
                        profileId
                );

        assertTrue(
                result.isPresent()
        );

        UserProfile actualProfile =
                result.orElseThrow();

        assertEquals(
                profileId,
                actualProfile.getId()
        );

        assertEquals(
                "Андрей",
                actualProfile.getFirstName()
        );

        assertEquals(
                "Андреев",
                actualProfile.getLastName()
        );

        assertEquals(
                "+7999990003",
                actualProfile.getPhone()
        );
    }

    @Test
    void findById_ShouldReturnEmpty_WhenUserProfileDoesNotExist() {

        Optional<UserProfile> result =
                userProfileRepository.findById(
                        Long.MAX_VALUE
                );

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void existsByPhone_ShouldReturnTrue_WhenPhoneExists() {

        User user = createUser(
                "profile-phone-exists-test"
        );

        createProfile(
                user,
                "Андрей",
                "Андреев",
                "+7999990004"
        );

        entityManager.flush();
        entityManager.clear();

        boolean result =
                userProfileRepository.existsByPhone(
                        "+7999990004"
                );

        assertTrue(
                result
        );
    }

    @Test
    void existsByPhone_ShouldReturnFalse_WhenPhoneDoesNotExist() {

        boolean result =
                userProfileRepository.existsByPhone(
                        "+79999999999"
                );

        assertFalse(
                result
        );
    }

    private User createUser(String login) {

        User user = User.builder()
                .login(login)
                .password("password")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .createdAt(
                        OffsetDateTime.parse(
                                "2026-09-25T08:00:00Z"
                        )
                )
                .build();

        entityManager.persist(user);

        return user;
    }

    private UserProfile createProfile(
            User user,
            String firstName,
            String lastName,
            String phone
    ) {

        UserProfile profile = buildProfile(
                user,
                firstName,
                lastName,
                phone
        );

        entityManager.persist(profile);

        return profile;
    }

    private UserProfile buildProfile(
            User user,
            String firstName,
            String lastName,
            String phone
    ) {

        return UserProfile.builder()
                .user(user)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .build();
    }
}