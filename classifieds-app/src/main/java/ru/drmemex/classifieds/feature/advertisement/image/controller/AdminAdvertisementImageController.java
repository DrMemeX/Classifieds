package ru.drmemex.classifieds.feature.advertisement.image.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.drmemex.classifieds.feature.advertisement.image.dto.response.AdvertisementImageResponse;
import ru.drmemex.classifieds.feature.advertisement.image.service.AdvertisementImageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin/advertisements/{advertisementId}/images")
public class AdminAdvertisementImageController {

    private final AdvertisementImageService advertisementImageService;

    @GetMapping
    public List<AdvertisementImageResponse> getByAdvertisementId(
            @PathVariable
            Long advertisementId
    ) {
        return advertisementImageService.getByAdvertisementIdForAdmin(
                advertisementId
        );
    }
}
