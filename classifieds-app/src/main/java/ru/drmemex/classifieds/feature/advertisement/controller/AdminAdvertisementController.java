package ru.drmemex.classifieds.feature.advertisement.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drmemex.classifieds.feature.advertisement.dto.response.AdvertisementResponse;
import ru.drmemex.classifieds.feature.advertisement.filter.AdvertisementFilter;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
import ru.drmemex.classifieds.feature.advertisement.service.AdvertisementService;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin/advertisements")
@RequiredArgsConstructor
public class AdminAdvertisementController {

    private final AdvertisementService advertisementService;

    @GetMapping("/{advertisementId}")
    public AdvertisementResponse getById(
            @PathVariable
            Long advertisementId
    ) {
        return advertisementService.getById(advertisementId);
    }

    @GetMapping("/seller/{sellerId}")
    public List<AdvertisementResponse> getSellerAdvertisement(
            @PathVariable
            Long sellerId,
            @RequestParam(required = false)
            AdvertisementStatus status
    ) {
        return advertisementService.getSellerAdvertisements(
                sellerId,
                status
        );
    }

    @GetMapping("/search")
    public List<AdvertisementResponse> search(
            AdvertisementFilter filter
    ) {
        return advertisementService.search(filter);
    }

    @PatchMapping("/{advertisementId}/block")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void block(
            @PathVariable
            Long advertisementId
    ) {
        advertisementService.block(advertisementId);
    }

    @PatchMapping("/{advertisementId}/unblock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unblock(
            @PathVariable
            Long advertisementId
    ) {
        advertisementService.unblock(advertisementId);
    }
}
