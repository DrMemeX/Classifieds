package ru.drmemex.classifieds.feature.advertisement.image.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drmemex.classifieds.feature.advertisement.image.dto.request.AdvertisementImageOrderRequest;
import ru.drmemex.classifieds.feature.advertisement.image.dto.request.AdvertisementImageRequest;
import ru.drmemex.classifieds.feature.advertisement.image.dto.response.AdvertisementImageResponse;
import ru.drmemex.classifieds.feature.advertisement.image.service.AdvertisementImageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@RequestMapping("/api/v1/advertisements/{advertisementId}/images")
public class AdvertisementImageController {

    private final AdvertisementImageService advertisementImageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdvertisementImageResponse create(
            @PathVariable
            Long advertisementId,

            @Valid @RequestBody
            AdvertisementImageRequest request
    ) {
        return advertisementImageService.create(
                advertisementId,
                request
        );
    }

    @GetMapping
    public List<AdvertisementImageResponse> getByAdvertisementId(
            @PathVariable
            Long advertisementId
    ) {
        return advertisementImageService.getByAdvertisementId(
                advertisementId
        );
    }

    @PatchMapping("/{imageId}/order")
    public AdvertisementImageResponse reorder(
            @PathVariable
            Long advertisementId,

            @PathVariable
            Long imageId,

            @Valid
            @RequestBody
            AdvertisementImageOrderRequest request
    ) {
        return advertisementImageService.reorder(
                advertisementId,
                imageId,
                request.displayOrder()
        );
    }

    @DeleteMapping("/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable
            Long advertisementId,

            @PathVariable
            Long imageId
    ) {
        advertisementImageService.delete(
                advertisementId,
                imageId
        );
    }
}