package ru.drmemex.classifieds.feature.user.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.drmemex.classifieds.feature.user.entity.UserProfile;
import ru.drmemex.classifieds.feature.user.repository.UserProfileRepository;

import java.util.Optional;

@Repository
public class UserProfileRepositoryImpl implements UserProfileRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserProfile save(UserProfile userProfile) {
        entityManager.persist(userProfile);
        return userProfile;
    }

    @Override
    public UserProfile update(UserProfile userProfile) {
        return entityManager.merge(userProfile);
    }

    @Override
    public Optional<UserProfile> findById(Long id) {
        return Optional.ofNullable(
                entityManager.find(UserProfile.class, id)
        );
    }

    @Override
    public boolean existsByPhone(String phone) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(up) FROM UserProfile up WHERE up.phone = :phone",
                        Long.class
                )
                .setParameter("phone", phone)
                .getSingleResult();

        return count > 0;
    }
}
