package ru.drmemex.classifieds.feature.user.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.feature.user.model.UserRole;
import ru.drmemex.classifieds.feature.user.model.UserStatus;
import ru.drmemex.classifieds.feature.user.repository.UserRepository;
import ru.drmemex.classifieds.integration.AbstractIntegrationTest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void save_ShouldSaveUser() {

        User user = buildUser(
                "user-save-test",
                UserRole.USER,
                UserStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-25T08:00:00Z"
                )
        );

        User savedUser =
                userRepository.save(user);

        entityManager.flush();
        entityManager.clear();

        User actualUser = entityManager.find(
                User.class,
                savedUser.getId()
        );

        assertNotNull(
                actualUser
        );

        assertEquals(
                "user-save-test",
                actualUser.getLogin()
        );

        assertEquals(
                "password",
                actualUser.getPassword()
        );

        assertEquals(
                UserRole.USER,
                actualUser.getRole()
        );

        assertEquals(
                UserStatus.ACTIVE,
                actualUser.getStatus()
        );
    }

    @Test
    void update_ShouldUpdateUser() {

        User user = createUser(
                "user-update-test"
        );

        entityManager.flush();
        entityManager.clear();

        OffsetDateTime blockedAt =
                OffsetDateTime.parse(
                        "2026-09-25T10:00:00Z"
                );

        user.setLogin("updated-user");
        user.setStatus(UserStatus.BLOCKED);
        user.setBlockedAt(blockedAt);

        User updatedUser =
                userRepository.update(user);

        entityManager.flush();
        entityManager.clear();

        User actualUser = entityManager.find(
                User.class,
                updatedUser.getId()
        );

        assertNotNull(
                actualUser
        );

        assertEquals(
                "updated-user",
                actualUser.getLogin()
        );

        assertEquals(
                UserStatus.BLOCKED,
                actualUser.getStatus()
        );

        assertEquals(
                blockedAt.toInstant(),
                actualUser.getBlockedAt().toInstant()
        );
    }

    @Test
    void findById_ShouldReturnUser() {

        User user = createUser(
                "user-find-test"
        );

        entityManager.flush();

        Long userId =
                user.getId();

        entityManager.clear();

        Optional<User> result =
                userRepository.findById(
                        userId
                );

        assertTrue(
                result.isPresent()
        );

        User actualUser =
                result.orElseThrow();

        assertEquals(
                userId,
                actualUser.getId()
        );

        assertEquals(
                "user-find-test",
                actualUser.getLogin()
        );

        assertEquals(
                UserRole.USER,
                actualUser.getRole()
        );

        assertEquals(
                UserStatus.ACTIVE,
                actualUser.getStatus()
        );
    }

    @Test
    void findById_ShouldReturnEmpty_WhenUserDoesNotExist() {

        Optional<User> result =
                userRepository.findById(
                        Long.MAX_VALUE
                );

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void findByLoginAndStatus_ShouldReturnUser() {

        createUser(
                "user-login-status-test"
        );

        entityManager.flush();
        entityManager.clear();

        Optional<User> result =
                userRepository.findByLoginAndStatus(
                        "user-login-status-test",
                        UserStatus.ACTIVE
                );

        assertTrue(
                result.isPresent()
        );

        User actualUser =
                result.orElseThrow();

        assertEquals(
                "user-login-status-test",
                actualUser.getLogin()
        );

        assertEquals(
                UserStatus.ACTIVE,
                actualUser.getStatus()
        );
    }

    @Test
    void findByLoginAndStatus_ShouldReturnEmpty_WhenStatusDoesNotMatch() {

        createUser(
                "user-wrong-status-test"
        );

        entityManager.flush();
        entityManager.clear();

        Optional<User> result =
                userRepository.findByLoginAndStatus(
                        "user-wrong-status-test",
                        UserStatus.BLOCKED
                );

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void existsByLogin_ShouldReturnTrue_WhenLoginExists() {

        createUser(
                "user-login-exists-test"
        );

        entityManager.flush();
        entityManager.clear();

        boolean result =
                userRepository.existsByLogin(
                        "user-login-exists-test"
                );

        assertTrue(
                result
        );
    }

    @Test
    void existsByLogin_ShouldReturnFalse_WhenLoginDoesNotExist() {

        boolean result =
                userRepository.existsByLogin(
                        "non-existent-login"
                );

        assertFalse(
                result
        );
    }

    @Test
    void findByFilters_ShouldReturnUsers_WhenFiltersAreNull() {

        createUser(
                "filter-null-user",
                UserRole.USER,
                UserStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2100-01-02T10:00:00Z"
                )
        );

        createUser(
                "filter-null-admin",
                UserRole.ADMIN,
                UserStatus.BLOCKED,
                OffsetDateTime.parse(
                        "2100-01-01T10:00:00Z"
                )
        );

        entityManager.flush();
        entityManager.clear();

        List<User> result =
                userRepository.findByFilters(
                        null,
                        null,
                        new PageRequest(
                                0,
                                100
                        )
                );

        List<String> logins = result.stream()
                .map(User::getLogin)
                .toList();

        assertTrue(
                logins.contains(
                        "filter-null-user"
                )
        );

        assertTrue(
                logins.contains(
                        "filter-null-admin"
                )
        );
    }

    @Test
    void findByFilters_ShouldFilterByStatus() {

        createUser(
                "filter-status-blocked-user",
                UserRole.USER,
                UserStatus.BLOCKED,
                OffsetDateTime.parse(
                        "2100-01-03T10:00:00Z"
                )
        );

        createUser(
                "filter-status-blocked-admin",
                UserRole.ADMIN,
                UserStatus.BLOCKED,
                OffsetDateTime.parse(
                        "2100-01-02T10:00:00Z"
                )
        );

        createUser(
                "filter-status-active",
                UserRole.USER,
                UserStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2100-01-01T10:00:00Z"
                )
        );

        entityManager.flush();
        entityManager.clear();

        List<User> result =
                userRepository.findByFilters(
                        UserStatus.BLOCKED,
                        null,
                        new PageRequest(
                                0,
                                100
                        )
                );

        List<String> logins = result.stream()
                .map(User::getLogin)
                .toList();

        assertTrue(
                result.stream()
                        .allMatch(user ->
                                user.getStatus()
                                        == UserStatus.BLOCKED
                        )
        );

        assertTrue(
                logins.contains(
                        "filter-status-blocked-user"
                )
        );

        assertTrue(
                logins.contains(
                        "filter-status-blocked-admin"
                )
        );

        assertFalse(
                logins.contains(
                        "filter-status-active"
                )
        );
    }

    @Test
    void findByFilters_ShouldFilterByRole() {

        createUser(
                "filter-role-user-1",
                UserRole.USER,
                UserStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2100-01-03T10:00:00Z"
                )
        );

        createUser(
                "filter-role-user-2",
                UserRole.USER,
                UserStatus.BLOCKED,
                OffsetDateTime.parse(
                        "2100-01-02T10:00:00Z"
                )
        );

        createUser(
                "filter-role-admin",
                UserRole.ADMIN,
                UserStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2100-01-01T10:00:00Z"
                )
        );

        entityManager.flush();
        entityManager.clear();

        List<User> result =
                userRepository.findByFilters(
                        null,
                        UserRole.USER,
                        new PageRequest(
                                0,
                                100
                        )
                );

        List<String> logins = result.stream()
                .map(User::getLogin)
                .toList();

        assertTrue(
                result.stream()
                        .allMatch(user ->
                                user.getRole()
                                        == UserRole.USER
                        )
        );

        assertTrue(
                logins.contains(
                        "filter-role-user-1"
                )
        );

        assertTrue(
                logins.contains(
                        "filter-role-user-2"
                )
        );

        assertFalse(
                logins.contains(
                        "filter-role-admin"
                )
        );
    }

    @Test
    void findByFilters_ShouldFilterByStatusAndRole() {

        createUser(
                "filter-both-match",
                UserRole.ADMIN,
                UserStatus.DELETED,
                OffsetDateTime.parse(
                        "2100-01-03T10:00:00Z"
                )
        );

        createUser(
                "filter-both-wrong-status",
                UserRole.ADMIN,
                UserStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2100-01-02T10:00:00Z"
                )
        );

        createUser(
                "filter-both-wrong-role",
                UserRole.USER,
                UserStatus.DELETED,
                OffsetDateTime.parse(
                        "2100-01-01T10:00:00Z"
                )
        );

        entityManager.flush();
        entityManager.clear();

        List<User> result =
                userRepository.findByFilters(
                        UserStatus.DELETED,
                        UserRole.ADMIN,
                        new PageRequest(
                                0,
                                100
                        )
                );

        assertEquals(
                1,
                result.size()
        );

        assertEquals(
                "filter-both-match",
                result.get(0).getLogin()
        );
    }

    @Test
    void findByFilters_ShouldSortAndApplyPagination() {

        OffsetDateTime newest =
                OffsetDateTime.parse(
                        "2100-01-04T10:00:00Z"
                );

        createUser(
                "filter-page-1",
                UserRole.ADMIN,
                UserStatus.DELETED,
                newest
        );

        createUser(
                "filter-page-2",
                UserRole.ADMIN,
                UserStatus.DELETED,
                newest
        );

        createUser(
                "filter-page-3",
                UserRole.ADMIN,
                UserStatus.DELETED,
                OffsetDateTime.parse(
                        "2100-01-03T10:00:00Z"
                )
        );

        createUser(
                "filter-page-4",
                UserRole.ADMIN,
                UserStatus.DELETED,
                OffsetDateTime.parse(
                        "2100-01-02T10:00:00Z"
                )
        );

        entityManager.flush();
        entityManager.clear();

        List<User> firstPage =
                userRepository.findByFilters(
                        UserStatus.DELETED,
                        UserRole.ADMIN,
                        new PageRequest(
                                0,
                                2
                        )
                );

        List<User> secondPage =
                userRepository.findByFilters(
                        UserStatus.DELETED,
                        UserRole.ADMIN,
                        new PageRequest(
                                1,
                                2
                        )
                );

        assertEquals(
                List.of(
                        "filter-page-2",
                        "filter-page-1"
                ),
                firstPage.stream()
                        .map(User::getLogin)
                        .toList()
        );

        assertEquals(
                List.of(
                        "filter-page-3",
                        "filter-page-4"
                ),
                secondPage.stream()
                        .map(User::getLogin)
                        .toList()
        );
    }

    @Test
    void countByFilters_ShouldCountAllUsers_WhenFiltersAreNull() {

        long countBefore =
                userRepository.countByFilters(
                        null,
                        null
                );

        createUser(
                "count-all-user",
                UserRole.USER,
                UserStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-25T09:00:00Z"
                )
        );

        createUser(
                "count-all-admin",
                UserRole.ADMIN,
                UserStatus.BLOCKED,
                OffsetDateTime.parse(
                        "2026-09-25T10:00:00Z"
                )
        );

        entityManager.flush();
        entityManager.clear();

        long result =
                userRepository.countByFilters(
                        null,
                        null
                );

        assertEquals(
                countBefore + 2,
                result
        );
    }

    @Test
    void countByFilters_ShouldCountUsersByStatus() {

        createUser(
                "count-status-blocked-1",
                UserRole.USER,
                UserStatus.BLOCKED,
                OffsetDateTime.parse(
                        "2026-09-25T09:00:00Z"
                )
        );

        createUser(
                "count-status-blocked-2",
                UserRole.ADMIN,
                UserStatus.BLOCKED,
                OffsetDateTime.parse(
                        "2026-09-25T10:00:00Z"
                )
        );

        createUser(
                "count-status-active",
                UserRole.USER,
                UserStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                )
        );

        entityManager.flush();
        entityManager.clear();

        long result =
                userRepository.countByFilters(
                        UserStatus.BLOCKED,
                        null
                );

        assertEquals(
                2L,
                result
        );
    }

    @Test
    void countByFilters_ShouldCountUsersByRole() {

        createUser(
                "count-role-admin-1",
                UserRole.ADMIN,
                UserStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-25T09:00:00Z"
                )
        );

        createUser(
                "count-role-admin-2",
                UserRole.ADMIN,
                UserStatus.BLOCKED,
                OffsetDateTime.parse(
                        "2026-09-25T10:00:00Z"
                )
        );

        createUser(
                "count-role-user",
                UserRole.USER,
                UserStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                )
        );

        entityManager.flush();
        entityManager.clear();

        long result =
                userRepository.countByFilters(
                        null,
                        UserRole.ADMIN
                );

        assertEquals(
                2L,
                result
        );
    }

    @Test
    void countByFilters_ShouldCountUsersByStatusAndRole() {

        createUser(
                "count-both-match-1",
                UserRole.ADMIN,
                UserStatus.DELETED,
                OffsetDateTime.parse(
                        "2026-09-25T09:00:00Z"
                )
        );

        createUser(
                "count-both-match-2",
                UserRole.ADMIN,
                UserStatus.DELETED,
                OffsetDateTime.parse(
                        "2026-09-25T10:00:00Z"
                )
        );

        createUser(
                "count-both-wrong-status",
                UserRole.ADMIN,
                UserStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-25T11:00:00Z"
                )
        );

        createUser(
                "count-both-wrong-role",
                UserRole.USER,
                UserStatus.DELETED,
                OffsetDateTime.parse(
                        "2026-09-25T12:00:00Z"
                )
        );

        entityManager.flush();
        entityManager.clear();

        long result =
                userRepository.countByFilters(
                        UserStatus.DELETED,
                        UserRole.ADMIN
                );

        assertEquals(
                2L,
                result
        );
    }

    private User createUser(String login) {

        return createUser(
                login,
                UserRole.USER,
                UserStatus.ACTIVE,
                OffsetDateTime.parse(
                        "2026-09-25T08:00:00Z"
                )
        );
    }

    private User createUser(
            String login,
            UserRole role,
            UserStatus status,
            OffsetDateTime createdAt
    ) {

        User user = buildUser(
                login,
                role,
                status,
                createdAt
        );

        entityManager.persist(user);

        return user;
    }

    private User buildUser(
            String login,
            UserRole role,
            UserStatus status,
            OffsetDateTime createdAt
    ) {

        return User.builder()
                .login(login)
                .password("password")
                .role(role)
                .status(status)
                .createdAt(createdAt)
                .build();
    }
}