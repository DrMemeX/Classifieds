package ru.drmemex.classifieds.feature.advertisement.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.common.util.string.StringNormalizer;
import ru.drmemex.classifieds.feature.advertisement.entity.Advertisement;
import ru.drmemex.classifieds.feature.advertisement.filter.AdvertisementFilter;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementSortField;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
import ru.drmemex.classifieds.feature.advertisement.model.SortDirection;
import ru.drmemex.classifieds.feature.advertisement.repository.AdvertisementRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static ru.drmemex.classifieds.common.util.pagination.PaginationUtils.applyPagination;

@Repository
public class AdvertisementRepositoryImpl implements AdvertisementRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Advertisement save(Advertisement advertisement) {
        entityManager.persist(advertisement);
        return advertisement;
    }

    @Override
    public Advertisement update(Advertisement advertisement) {
        return entityManager.merge(advertisement);
    }

    @Override
    public Optional<Advertisement> findById(Long id) {
        return Optional.ofNullable(
                entityManager.find(Advertisement.class, id)
        );
    }

    @Override
    public List<Advertisement> findBySellerId(
            Long sellerId,
            AdvertisementStatus status
    ) {
        return entityManager.createQuery(
                        """
                                SELECT a
                                FROM Advertisement a
                                WHERE a.seller.id = :sellerId
                                AND a.advertisementStatus = :status
                                ORDER BY a.createdAt DESC, a.id DESC
                                """,
                        Advertisement.class
                )
                .setParameter("sellerId", sellerId)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<Advertisement> findBySellerId(
            Long sellerId,
            AdvertisementStatus status,
            PageRequest pageRequest
    ) {

        StringBuilder jpql = new StringBuilder(
                        """
                        SELECT a
                        FROM Advertisement a
                        WHERE a.seller.id = :sellerId
                        """
        );

        if (status != null) {
            jpql.append(" AND a.advertisementStatus = :status");
        }

        jpql.append(" ORDER BY a.createdAt DESC, a.id DESC");

        TypedQuery<Advertisement> query = entityManager.createQuery(
                jpql.toString(),
                Advertisement.class
        );

        query.setParameter("sellerId", sellerId);

        if (status != null) {
            query.setParameter("status", status);
        }

        applyPagination(
                query,
                pageRequest
        );

        return query.getResultList();
    }

    @Override
    public long countBySellerId(
            Long sellerId,
            AdvertisementStatus status
    ) {

        StringBuilder jpql = new StringBuilder(
                        """
                        SELECT COUNT(a)
                        FROM Advertisement a
                        WHERE a.seller.id = :sellerId
                        """
        );

        if (status != null) {
            jpql.append(" AND a.advertisementStatus = :status");
        }

        TypedQuery<Long> query = entityManager.createQuery(
                jpql.toString(),
                Long.class
        );

        query.setParameter("sellerId", sellerId);

        if (status != null) {
            query.setParameter("status", status);
        }

        return query.getSingleResult();
    }

    @Override
    public List<Advertisement> findByFilters(
            AdvertisementFilter filter,
            PageRequest pageRequest
    ) {

        String locality = filter.locality() == null
                ? null
                : StringNormalizer.normalizeComparable(filter.locality());

        String title = filter.title() == null
                ? null
                : StringNormalizer.normalizeComparable(filter.title());

        StringBuilder sql = new StringBuilder(
                        """
                        SELECT *
                        FROM advertisements
                        """
        );

        sql.append(
                buildWhereClause(
                        filter,
                        locality,
                        title
                )
        );

        sql.append(" ORDER BY ");

        if (title != null && !title.isBlank()) {
            sql.append(
                    "similarity(LOWER(title), :title) DESC, "
            );
        }

        if (filter.sortField() == AdvertisementSortField.PRICE) {
            sql.append("price ");
        } else {
            sql.append("created_at ");
        }

        if (filter.sortDirection() == SortDirection.ASC) {
            sql.append("ASC, id ASC");
        } else {
            sql.append("DESC, id DESC");
        }

        Query query = entityManager.createNativeQuery(
                sql.toString(),
                Advertisement.class
        );

        setFilterParameters(
                query,
                filter,
                locality,
                title
        );

        applyPagination(
                query,
                pageRequest
        );

        @SuppressWarnings("unchecked")
        List<Advertisement> result = query.getResultList();

        return result;
    }

    @Override
    public long countByFilters(AdvertisementFilter filter) {

        String locality = filter.locality() == null
                ? null
                : StringNormalizer.normalizeComparable(filter.locality());

        String title = filter.title() == null
                ? null
                : StringNormalizer.normalizeComparable(filter.title());

        StringBuilder sql = new StringBuilder(
                        """
                        SELECT COUNT(*)
                        FROM advertisements
                        """
        );

        sql.append(
                buildWhereClause(
                        filter,
                        locality,
                        title
                )
        );

        Query query = entityManager.createNativeQuery(
                sql.toString()
        );

        setFilterParameters(
                query,
                filter,
                locality,
                title
        );

        return ((Number) query.getSingleResult()).longValue();
    }

    @Override
    public long countBySellerIdAndCreatedAtAfter(
            Long sellerId,
            OffsetDateTime createdAfter
    ) {
        return entityManager.createQuery(
                        """
                                SELECT COUNT(a)
                                FROM Advertisement a
                                WHERE a.seller.id = :sellerId
                                AND a.createdAt >= :createdAfter
                                """,
                        Long.class
                )
                .setParameter("sellerId", sellerId)
                .setParameter("createdAfter", createdAfter)
                .getSingleResult();
    }

    private String buildWhereClause(
            AdvertisementFilter filter,
            String locality,
            String title
    ) {

        StringBuilder sql = new StringBuilder(
                        """
                        WHERE 1 = 1
                        """
        );

        if (filter.status() != null) {
            sql.append(" AND status = :status");
        }

        if (filter.categoryIds() != null
                && !filter.categoryIds().isEmpty()) {
            sql.append(" AND category_id IN (:categoryIds)");
        }

        if (filter.regionIds() != null
                && !filter.regionIds().isEmpty()) {
            sql.append(" AND region_id IN (:regionIds)");
        }

        if (locality != null && !locality.isBlank()) {
            sql.append(" AND LOWER(locality) % :locality");
        }

        if (title != null && !title.isBlank()) {
            sql.append(" AND LOWER(title) % :title");
        }

        if (filter.minPrice() != null) {
            sql.append(" AND price >= :minPrice");
        }

        if (filter.maxPrice() != null) {
            sql.append(" AND price <= :maxPrice");
        }

        return sql.toString();
    }

    private void setFilterParameters(
            Query query,
            AdvertisementFilter filter,
            String locality,
            String title
    ) {

        if (filter.status() != null) {
            query.setParameter(
                    "status",
                    filter.status().name()
            );
        }

        if (filter.categoryIds() != null
                && !filter.categoryIds().isEmpty()) {
            query.setParameter(
                    "categoryIds",
                    filter.categoryIds()
            );
        }

        if (filter.regionIds() != null
                && !filter.regionIds().isEmpty()) {
            query.setParameter(
                    "regionIds",
                    filter.regionIds()
            );
        }

        if (locality != null && !locality.isBlank()) {
            query.setParameter(
                    "locality",
                    locality
            );
        }

        if (title != null && !title.isBlank()) {
            query.setParameter(
                    "title",
                    title
            );
        }

        if (filter.minPrice() != null) {
            query.setParameter(
                    "minPrice",
                    filter.minPrice()
            );
        }

        if (filter.maxPrice() != null) {
            query.setParameter(
                    "maxPrice",
                    filter.maxPrice()
            );
        }
    }
}