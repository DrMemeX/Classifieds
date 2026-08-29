package ru.drmemex.classifieds.feature.advertisement.controller;

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
import ru.drmemex.classifieds.feature.advertisement.dto.request.AdvertisementRequest;
import ru.drmemex.classifieds.feature.advertisement.dto.request.AdvertisementUpdateRequest;
import ru.drmemex.classifieds.feature.advertisement.dto.response.AdvertisementResponse;
import ru.drmemex.classifieds.feature.advertisement.filter.AdvertisementFilter;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
import ru.drmemex.classifieds.feature.advertisement.service.AdvertisementService;

import java.util.List;

@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping("/api/v1/advertisement")
@RequiredArgsConstructor
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdvertisementResponse create(
            @Valid
            @RequestBody
            AdvertisementRequest request
    ) {
        return advertisementService.create(request);
    }

    @PatchMapping("/{advertisementId}")
    public AdvertisementResponse update(
            @PathVariable
            Long advertisementId,
            @Valid
            @RequestBody
            AdvertisementUpdateRequest request
    ) {
        return advertisementService.update(
                advertisementId,
                request
        );
    }

    @GetMapping("/{advertisementId}")
    public AdvertisementResponse getById(
            @PathVariable
            Long advertisementId
    ) {
        return advertisementService.getById(advertisementId);
    }

    @GetMapping("/seller/{sellerId}")
    public List<AdvertisementResponse> getSellerAdvertisements(
            @PathVariable
            Long sellerId
    ) {
        return advertisementService.getSellerAdvertisements(
                sellerId,
                AdvertisementStatus.ACTIVE
        );
    }

    @GetMapping("/search")
    public List<AdvertisementResponse> search(
            AdvertisementFilter filter
    ) {
        return advertisementService.search(filter);
    }

    @PatchMapping("/{advertisementId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(
            @PathVariable
            Long advertisementId
    ) {
        advertisementService.activate(advertisementId);
    }

    @PatchMapping("/{advertisementId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(
            @PathVariable
            Long advertisementId
    ) {
        advertisementService.deactivate(advertisementId);
    }

    @DeleteMapping("/{advertisementId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable
            Long advertisementId
    ) {
        advertisementService.delete(advertisementId);
    }
}
