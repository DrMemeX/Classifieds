package ru.drmemex.classifieds.feature.region.repository.impl;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.drmemex.classifieds.feature.region.entity.Region;
import ru.drmemex.classifieds.feature.region.repository.RegionRepository;
import ru.drmemex.classifieds.integration.AbstractIntegrationTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegionRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAll_ShouldReturnRegionsSortByName() {

        List<Region> regions =
                regionRepository.findAll();

        assertFalse(
                regions.isEmpty()
        );

        List<String> actualNames = regions.stream()
                .map(Region::getName)
                .toList();

        List<String> sortedNames = actualNames.stream()
                .sorted()
                .toList();

        assertEquals(
                sortedNames,
                actualNames
        );
    }

    @Test
    void findById_ShouldReturnRegion() {

        Region expectedRegion = entityManager.createQuery(
                        "SELECT r FROM Region r WHERE r.name = :name",
                        Region.class
                )
                .setParameter(
                        "name",
                        "Москва"
                )
                .getSingleResult();

        Optional<Region> result =
                regionRepository.findById(
                        expectedRegion.getId()
                );

        assertTrue(
                result.isPresent()
        );

        Region actualRegion =
                result.orElseThrow();

        assertEquals(
                expectedRegion.getId(),
                actualRegion.getId()
        );

        assertEquals(
                expectedRegion.getName(),
                actualRegion.getName()
        );
    }

    @Test
    void findById_ShouldReturnEmpty_WhenRegionDoesNotExists() {

        Optional<Region> result =
                regionRepository.findById(
                        Long.MAX_VALUE
                );

        assertTrue(
                result.isEmpty()
        );
    }

    @Test
    void findByName_ShouldReturnRegion_WhenNameContainsTypo() {

        List<Region> result =
                regionRepository.findByName(
                        "масква"
                );

        assertFalse(
                result.isEmpty()
        );

        assertEquals(
                "Москва",
                result.get(0).getName()
        );
    }
}