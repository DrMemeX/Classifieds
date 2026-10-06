package ru.drmemex.classifieds.feature.user.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.feature.user.model.UserRole;
import ru.drmemex.classifieds.feature.user.model.UserStatus;
import ru.drmemex.classifieds.feature.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static ru.drmemex.classifieds.common.util.pagination.PaginationUtils.applyPagination;

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
    public Optional<User> findByLoginAndStatus(
            String login,
            UserStatus status
    ) {
        return entityManager.createQuery(
                        """
                                SELECT u
                                FROM User u
                                WHERE u.login = :login
                                AND u.status = :status
                                """,
                        User.class
                )
                .setParameter("login", login)
                .setParameter("status", status)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public boolean existsByLogin(String login) {
        Long count = entityManager.createQuery(
                        """
                                SELECT COUNT(u)
                                FROM User u
                                WHERE u.login = :login
                                """,
                        Long.class
                )
                .setParameter("login", login)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public List<User> findByFilters(
            UserStatus status,
            UserRole role,
            PageRequest pageRequest
    ) {

        StringBuilder jpql = new StringBuilder(
                "SELECT u FROM User u WHERE 1 = 1"
        );

        if (status != null) {
            jpql.append(" AND u.status = :status");
        }

        if (role != null) {
            jpql.append(" AND u.role = :role");
        }

        jpql.append(
                " ORDER BY u.createdAt DESC, u.id DESC"
        );

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

        applyPagination(
                query,
                pageRequest
        );

        return query.getResultList();
    }

    @Override
    public long countByFilters(
            UserStatus status,
            UserRole role
    ) {

        StringBuilder jpql = new StringBuilder(
                "SELECT COUNT(u) FROM User u WHERE 1 = 1"
        );

        if (status != null) {
            jpql.append(" AND u.status = :status");
        }

        if (role != null) {
            jpql.append(" AND u.role = :role");
        }

        TypedQuery<Long> query = entityManager.createQuery(
                jpql.toString(),
                Long.class
        );

        if (status != null) {
            query.setParameter("status", status);
        }

        if (role != null) {
            query.setParameter("role", role);
        }

        return query.getSingleResult();
    }
}