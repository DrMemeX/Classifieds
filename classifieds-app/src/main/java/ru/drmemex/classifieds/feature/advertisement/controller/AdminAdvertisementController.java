package ru.drmemex.classifieds.feature.advertisement.controller;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.drmemex.classifieds.common.util.pagination.dto.PageRequest;
import ru.drmemex.classifieds.common.util.pagination.dto.PageResponse;
import ru.drmemex.classifieds.feature.advertisement.dto.response.AdvertisementResponse;
import ru.drmemex.classifieds.feature.advertisement.filter.AdvertisementFilter;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
import ru.drmemex.classifieds.feature.advertisement.service.AdvertisementService;

@Validated
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
    public PageResponse<AdvertisementResponse> getSellerAdvertisement(
            @PathVariable
            Long sellerId,
            @RequestParam(required = false)
            AdvertisementStatus status,
            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,
            @RequestParam(defaultValue = "20")
            @Min(1)
            @Max(100)
            int size
    ) {
        return advertisementService.getSellerAdvertisements(
                sellerId,
                status,
                new PageRequest(
                        page,
                        size
                )
        );
    }

    @GetMapping("/search")
    public PageResponse<AdvertisementResponse> search(
            AdvertisementFilter filter,
            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,
            @RequestParam(defaultValue = "20")
            @Min(1)
            @Max(100)
            int size
    ) {
        return advertisementService.search(
                filter,
                new PageRequest(
                        page,
                        size
                )
        );
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