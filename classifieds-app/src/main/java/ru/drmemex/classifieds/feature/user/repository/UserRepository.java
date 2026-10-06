package ru.drmemex.classifieds.feature.user.repository;

import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.feature.user.model.UserRole;
import ru.drmemex.classifieds.feature.user.model.UserStatus;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    User update(User user);

    Optional<User> findById(Long id);

    Optional<User> findByLoginAndStatus(
            String login,
            UserStatus status
    );

    boolean existsByLogin(String login);

    List<User> findByFilters(
            UserStatus status,
            UserRole role,
            PageRequest pageRequest
    );

    long countByFilters(
            UserStatus status,
            UserRole role
    );
}
