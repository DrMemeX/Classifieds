package ru.drmemex.classifieds.feature.user.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.feature.user.model.UserRole;
import ru.drmemex.classifieds.feature.user.model.UserStatus;
import ru.drmemex.classifieds.feature.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public User save(User user) {
        entityManager.persist(user);
        return user;
    }

    @Override
    public User update(User user) {
        return entityManager.merge(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(
                entityManager.find(User.class, id)
        );
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.login = :login",
                        User.class
                )
                .setParameter("login", login)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<User> findByLoginAndStatus(String login, UserStatus status) {
        return entityManager.createQuery(
                "SELECT u FROM User u WHERE u.login = :login AND u.status = :status",
                User.class
        )
                .setParameter("login", login)
                .setParameter("status", status)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return entityManager.createQuery(
                        "SELECT u FROM User u ORDER BY u.createdAt DESC",
                        User.class
                )
                .getResultList();
    }

    @Override
    public List<User> findByRole(UserRole role) {
        return entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.role = :role ORDER BY u.createdAt DESC",
                        User.class
                )
                .setParameter("role", role)
                .getResultList();
    }

    @Override
    public List<User> findByStatus(UserStatus status) {
        return entityManager.createQuery(
                        "SELECT u FROM User u WHERE u.status = :status ORDER BY u.createdAt DESC",
                        User.class
                )
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public boolean existsByLogin(String login) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.login = :login",
                        Long.class
                )
                .setParameter("login", login)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public List<User> findByFilters(UserStatus status, UserRole role) {

        StringBuilder jpql = new StringBuilder(
                "SELECT u FROM User u WHERE 1 = 1"
        );

        if (status != null) {
            jpql.append(" AND u.status = :status");
        }

        if (role != null) {
            jpql.append(" AND u.role = :role");
        }

        jpql.append(" ORDER BY u.createdAt DESC");

        TypedQuery<User> query = entityManager.createQuery(
                jpql.toString(),
                User.class
        );

        if (status != null) {
            query.setParameter("status", status);
        }

        if (role != null) {
            query.setParameter("role", role);
        }

        return query.getResultList();
    }
}
