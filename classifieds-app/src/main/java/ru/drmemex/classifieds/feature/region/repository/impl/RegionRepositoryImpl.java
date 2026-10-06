package ru.drmemex.classifieds.feature.region.repository.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.drmemex.classifieds.feature.region.entity.Region;
import ru.drmemex.classifieds.feature.region.repository.RegionRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RegionRepositoryImpl implements RegionRepository {

    private final EntityManager entityManager;

    @Override
    public List<Region> findAll() {
        return entityManager.createQuery(
                        "SELECT r FROM Region r ORDER BY r.name",
                        Region.class
                )
                .getResultList();
    }

    @Override
    public Optional<Region> findById(Long id) {
        return Optional.ofNullable(
                entityManager.find(Region.class, id)
        );
    }


    @Override
    public List<Region> findByName(String name) {
        return entityManager.createNativeQuery(
                """
                        SELECT *
                        FROM regions
                        WHERE LOWER(name) % :name
                        ORDER BY similarity(LOWER(name), :name) DESC
                        """, Region.class
        )
                .setParameter("name", name)
                .getResultList();
    }
}
