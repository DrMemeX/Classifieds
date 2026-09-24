package ru.drmemex.classifieds.feature.region.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.drmemex.classifieds.feature.region.dto.RegionResponse;
import ru.drmemex.classifieds.feature.region.entity.Region;
import ru.drmemex.classifieds.feature.region.exception.RegionNotFoundException;
import ru.drmemex.classifieds.feature.region.mapper.RegionMapper;
import ru.drmemex.classifieds.feature.region.repository.RegionRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegionServiceImplTest {

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private RegionMapper regionMapper;

    @InjectMocks
    private RegionServiceImpl regionService;

    @Test
    void getAll_ShouldReturnAllRegions() {

        Region firstRegion = mock(Region.class);
        Region secondRegion = mock(Region.class);

        RegionResponse firstResponse = mock(RegionResponse.class);
        RegionResponse secondResponse = mock(RegionResponse.class);

        when(regionRepository.findAll())
                .thenReturn(
                        List.of(
                                firstRegion,
                                secondRegion
                        )
                );

        when(regionMapper.toResponse(firstRegion))
                .thenReturn(firstResponse);

        when(regionMapper.toResponse(secondRegion))
                .thenReturn(secondResponse);

        List<RegionResponse> result =
                regionService.getAll();

        assertEquals(
                List.of(
                        firstResponse,
                        secondResponse
                ),
                result
        );

        verify(regionRepository)
                .findAll();
    }

    @Test
    void getById_ShouldReturnRegion() {

        Region region = mock(Region.class);
        RegionResponse expectedResponse = mock(RegionResponse.class);

        when(regionRepository.findById(1L))
                .thenReturn(Optional.of(region));

        when(regionMapper.toResponse(region))
                .thenReturn(expectedResponse);

        RegionResponse result =
                regionService.getById(1L);

        assertEquals(
                expectedResponse,
                result
        );

        verify(regionRepository)
                .findById(1L);

        verify(regionMapper)
                .toResponse(region);
    }

    @Test
    void getById_ShouldThrowException_WhenRegionNotFound() {

        when(regionRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RegionNotFoundException.class,
                () -> regionService.getById(1L)
        );

        verify(regionMapper, never())
                .toResponse(org.mockito.ArgumentMatchers.any(Region.class));
    }

    @Test
    void getByName_ShouldReturnRegionsByName() {

        Region firstRegion = mock(Region.class);
        Region secondRegion = mock(Region.class);

        RegionResponse firstResponse = mock(RegionResponse.class);
        RegionResponse secondResponse = mock(RegionResponse.class);

        when(regionRepository.findByName("москва"))
                .thenReturn(
                        List.of(
                                firstRegion,
                                secondRegion
                        )
                );

        when(regionMapper.toResponse(firstRegion))
                .thenReturn(firstResponse);

        when(regionMapper.toResponse(secondRegion))
                .thenReturn(secondResponse);

        List<RegionResponse> result =
                regionService.getByName("  МОСКВА  ");

        assertEquals(
                List.of(
                        firstResponse,
                        secondResponse
                ),
                result
        );

        verify(regionRepository)
                .findByName("москва");
    }
}