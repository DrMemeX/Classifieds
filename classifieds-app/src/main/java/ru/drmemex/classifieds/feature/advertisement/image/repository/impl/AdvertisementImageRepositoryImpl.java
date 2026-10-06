package ru.drmemex.classifieds.feature.advertisement.image.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import ru.drmemex.classifieds.feature.advertisement.image.entity.AdvertisementImage;
import ru.drmemex.classifieds.feature.advertisement.image.repository.AdvertisementImageRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class AdvertisementImageRepositoryImpl implements AdvertisementImageRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public AdvertisementImage save(AdvertisementImage image) {
        entityManager.persist(image);
        return image;
    }

    @Override
    public Optional<AdvertisementImage> findById(Long id) {
        return Optional.ofNullable(
                entityManager.find(AdvertisementImage.class, id)
        );
    }

    @Override
    public List<AdvertisementImage> findByAdvertisementId(Long advertisementId) {
        TypedQuery<AdvertisementImage> query = entityManager.createQuery(
                """
                        SELECT i
                        FROM AdvertisementImage i
                        WHERE i.advertisement.id = :advertisementId
                        ORDER BY i.displayOrder ASC
                        """,
                AdvertisementImage.class
        );

        query.setParameter("advertisementId", advertisementId);

        return query.getResultList();
    }

    @Override
    public long countByAdvertisementId(Long advertisementId) {
        return entityManager.createQuery(
                        """
                                SELECT COUNT(i)
                                FROM AdvertisementImage i
                                WHERE i.advertisement.id = :advertisementId
                                """,
                        Long.class
                )
                .setParameter("advertisementId", advertisementId)
                .getSingleResult();
    }

    @Override
    public void delete(AdvertisementImage image) {
        entityManager.remove(image);
    }
}