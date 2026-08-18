package ru.drmemex.classifieds.feature.region.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.drmemex.classifieds.feature.region.dto.RegionResponse;
import ru.drmemex.classifieds.feature.region.service.RegionService;

import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/v1/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @GetMapping
    public List<RegionResponse> getRegions(
            @RequestParam(required = false)
            String name
    ) {
        if (name == null || name.isBlank()) {
            return regionService.getAll();
        }

        return regionService.findByName(name);
    }

    @GetMapping("/{id}")
    public RegionResponse getById(
            @PathVariable
            Long id
    ) {
        return regionService.getById(id);
    }
}
