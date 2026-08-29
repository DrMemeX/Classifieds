package ru.drmemex.classifieds.feature.advertisement.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.drmemex.classifieds.common.util.string.StringNormalizer;
import ru.drmemex.classifieds.feature.advertisement.entity.Advertisement;
import ru.drmemex.classifieds.feature.advertisement.filter.AdvertisementFilter;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementSortField;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
import ru.drmemex.classifieds.feature.advertisement.model.SortDirection;
import ru.drmemex.classifieds.feature.advertisement.repository.AdvertisementRepository;

import java.util.List;
import java.util.Optional;

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

        jpql.append(" ORDER BY a.createdAt DESC");

        TypedQuery<Advertisement> query = entityManager.createQuery(
                jpql.toString(),
                Advertisement.class
        );

        query.setParameter("sellerId", sellerId);

        if (status != null) {
            query.setParameter("status", status);
        }

        return query.getResultList();
    }

    @Override
    public List<Advertisement> findByFilters(AdvertisementFilter filter) {

        String locality = filter.locality() == null
                ? null
                : StringNormalizer.normalizeComparable(filter.locality());

        String title = filter.title() == null
                ? null
                : StringNormalizer.normalizeComparable(filter.title());

        boolean hasStatus = filter.status() != null;

        boolean hasCategories =
                filter.categoryIds() != null && !filter.categoryIds().isEmpty();
        boolean hasRegions =
                filter.regionIds() != null && !filter.regionIds().isEmpty();

        boolean hasLocality = locality != null && !locality.isBlank();
        boolean hasTitle = title != null && !title.isBlank();

        boolean hasMinPrice = filter.minPrice() != null;
        boolean hasMaxPrice = filter.maxPrice() != null;

        StringBuilder sql = new StringBuilder(
                """
                SELECT *
                FROM advertisements
                WHERE 1 = 1
                """
        );

        if (hasStatus) {
            sql.append(" AND status = :status");
        }

        if (hasCategories) {
            sql.append(" AND category_id IN (:categoryIds)");
        }

        if (hasRegions) {
            sql.append(" AND region_id IN (:regionIds)");
        }

        if (hasLocality) {
            sql.append(" AND LOWER(locality) % :locality");
        }

        if (hasTitle) {
            sql.append(" AND LOWER(title) % :title");
        }

        if (hasMinPrice) {
            sql.append(" AND price >= :minPrice");
        }

        if (hasMaxPrice) {
            sql.append(" AND price <= :maxPrice");
        }

        sql.append(" ORDER BY ");

        if (hasTitle) {
            sql.append("similarity(LOWER(title), :title) DESC, ");
        }

        if (filter.sortField() == AdvertisementSortField.PRICE) {
            sql.append("price ");
        } else {
            sql.append("created_at ");
        }

        if (filter.sortDirection() == SortDirection.ASC) {
            sql.append("ASC");
        } else {
            sql.append("DESC");
        }

        Query query = entityManager.createNativeQuery(
                sql.toString(),
                Advertisement.class
        );

        if (hasStatus) {
            query.setParameter("status", filter.status().name());
        }

        if (hasCategories) {
            query.setParameter("categoryIds", filter.categoryIds());
        }

        if (hasRegions) {
            query.setParameter("regionIds", filter.regionIds());
        }

        if (hasLocality) {
            query.setParameter("locality", locality);
        }

        if (hasTitle) {
            query.setParameter("title", title);
        }

        if (hasMinPrice) {
            query.setParameter("minPrice", filter.minPrice());
        }

        if (hasMaxPrice) {
            query.setParameter("maxPrice", filter.maxPrice());
        }

        @SuppressWarnings("unchecked")
        List<Advertisement> result = query.getResultList();

        return result;
    }
}
