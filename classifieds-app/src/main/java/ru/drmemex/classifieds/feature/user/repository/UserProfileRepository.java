package ru.drmemex.classifieds.feature.user.repository;

import ru.drmemex.classifieds.feature.user.entity.UserProfile;

import java.util.Optional;

public interface UserProfileRepository {

    UserProfile save(UserProfile userProfile);

    UserProfile update(UserProfile userProfile);

    Optional<UserProfile> findById(Long id);

    boolean existsByPhone(String phone);
}
