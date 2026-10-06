package ru.drmemex.classifieds.feature.advertisement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.drmemex.classifieds.feature.advertisement.dto.request.AdvertisementRequest;
import ru.drmemex.classifieds.feature.advertisement.dto.request.AdvertisementUpdateRequest;
import ru.drmemex.classifieds.feature.advertisement.dto.response.AdvertisementResponse;
import ru.drmemex.classifieds.feature.advertisement.entity.Advertisement;
import ru.drmemex.classifieds.feature.advertisement.exception.AdminCannotCreateAdvertisementException;
import ru.drmemex.classifieds.feature.advertisement.exception.AdvertisementAccessDeniedException;
import ru.drmemex.classifieds.feature.advertisement.exception.AdvertisementCategoryInactiveException;
import ru.drmemex.classifieds.feature.advertisement.exception.AdvertisementNotFoundException;
import ru.drmemex.classifieds.feature.advertisement.exception.InvalidAdvertisementStatusException;
import ru.drmemex.classifieds.feature.advertisement.filter.AdvertisementFilter;
import ru.drmemex.classifieds.feature.advertisement.mapper.AdvertisementMapper;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
import ru.drmemex.classifieds.feature.advertisement.repository.AdvertisementRepository;
import ru.drmemex.classifieds.feature.advertisement.service.AdvertisementService;
import ru.drmemex.classifieds.feature.category.entity.Category;
import ru.drmemex.classifieds.feature.category.exception.CategoryNotFoundException;
import ru.drmemex.classifieds.feature.category.repository.CategoryRepository;
import ru.drmemex.classifieds.feature.region.entity.Region;
import ru.drmemex.classifieds.feature.region.exception.RegionNotFoundException;
import ru.drmemex.classifieds.feature.region.repository.RegionRepository;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.feature.user.model.UserRole;
import ru.drmemex.classifieds.security.provider.CurrentUserProvider;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements AdvertisementService {

    private final AdvertisementRepository advertisementRepository;

    private final CategoryRepository categoryRepository;

    private final RegionRepository regionRepository;

    private final AdvertisementMapper advertisementMapper;

    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional
    public AdvertisementResponse create(AdvertisementRequest request) {

        User seller = currentUserProvider.getCurrentUser();

        if (seller.getRole() == UserRole.ADMIN) {
            throw new AdminCannotCreateAdvertisementException();
        }

        Category category = categoryRepository.findActiveById(request.categoryId())
                .orElseThrow(() ->
                        new CategoryNotFoundException(request.categoryId()));

        Region region = regionRepository.findById(request.regionId())
                .orElseThrow(RegionNotFoundException::new);

        Advertisement advertisement = advertisementMapper.toEntity(request);

        advertisement.setSeller(seller);
        advertisement.setCategory(category);
        advertisement.setRegion(region);
        advertisement.setAdvertisementStatus(AdvertisementStatus.ACTIVE);
        advertisement.setCreatedAt(OffsetDateTime.now());

        return advertisementMapper.toResponse(
                advertisementRepository.save(advertisement)
        );
    }

    @Override
    @Transactional
    public AdvertisementResponse update(
            Long advertisementId,
            AdvertisementUpdateRequest request
    ) {

        Advertisement advertisement = getAdvertisement(advertisementId);

        checkOwner(advertisement);

        if (advertisement.getAdvertisementStatus() != AdvertisementStatus.ACTIVE
                && advertisement.getAdvertisementStatus() != AdvertisementStatus.INACTIVE) {
            throw new InvalidAdvertisementStatusException();
        }

        if (request.categoryId() != null) {

            Category category = categoryRepository.findActiveById(
                            request.categoryId()
                    )
                    .orElseThrow(() ->
                            new CategoryNotFoundException(
                                    request.categoryId()
                            )
                    );

            advertisement.setCategory(category);
        }

        if (request.regionId() != null) {

            Region region = regionRepository.findById(
                            request.regionId()
                    )
                    .orElseThrow(RegionNotFoundException::new);

            advertisement.setRegion(region);
        }

        if (request.locality() != null) {
            advertisement.setLocality(request.locality());
        }

        if (request.title() != null) {
            advertisement.setTitle(request.title());
        }

        if (request.description() != null) {
            advertisement.setDescription(request.description());
        }

        if (request.price() != null) {
            advertisement.setPrice(request.price());
        }

        advertisement.setUpdatedAt(OffsetDateTime.now());

        return advertisementMapper.toResponse(
                advertisementRepository.update(advertisement)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AdvertisementResponse getById(Long advertisementId) {

        Advertisement advertisement = getAdvertisement(advertisementId);

        User currentUser = currentUserProvider.getCurrentUser();

        if (advertisement.getAdvertisementStatus() != AdvertisementStatus.ACTIVE
                && currentUser.getRole() != UserRole.ADMIN) {
            throw new AdvertisementAccessDeniedException(
                    advertisementId
            );
        }

        return advertisementMapper.toResponse(advertisement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdvertisementResponse> getSellerAdvertisements(
            Long sellerId,
            AdvertisementStatus status
    ) {

        User currentUser = currentUserProvider.getCurrentUser();

        AdvertisementStatus requestedStatus = status;

        if (currentUser.getRole() != UserRole.ADMIN) {
            requestedStatus = AdvertisementStatus.ACTIVE;
        }

        return advertisementRepository.findBySellerId(
                        sellerId,
                        requestedStatus
                )
                .stream()
                .map(advertisementMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdvertisementResponse> search(AdvertisementFilter filter) {

        User currentUser = currentUserProvider.getCurrentUser();

        AdvertisementFilter actualFilter = filter;

        if (currentUser.getRole() != UserRole.ADMIN) {
            actualFilter = new AdvertisementFilter(
                    AdvertisementStatus.ACTIVE,
                    filter.categoryIds(),
                    filter.regionIds(),
                    filter.locality(),
                    filter.title(),
                    filter.minPrice(),
                    filter.maxPrice(),
                    filter.sortField(),
                    filter.sortDirection()
            );
        }

        return advertisementRepository.findByFilters(actualFilter)
                .stream()
                .map(advertisementMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void activate(Long advertisementId) {

        Advertisement advertisement = getAdvertisement(advertisementId);

        checkOwner(advertisement);

        if (advertisement.getAdvertisementStatus()
                != AdvertisementStatus.INACTIVE) {
            throw new InvalidAdvertisementStatusException();
        }

        if (!advertisement.getCategory().getActive()) {
            throw new AdvertisementCategoryInactiveException(
                    advertisementId
            );
        }

        advertisement.setAdvertisementStatus(
                AdvertisementStatus.ACTIVE
        );
        advertisement.setUpdatedAt(OffsetDateTime.now());

        advertisementRepository.update(advertisement);
    }

    @Override
    @Transactional
    public void deactivate(Long advertisementId) {

        Advertisement advertisement = getAdvertisement(advertisementId);

        checkOwner(advertisement);

        if (advertisement.getAdvertisementStatus()
                != AdvertisementStatus.ACTIVE) {
            throw new InvalidAdvertisementStatusException();
        }

        advertisement.setAdvertisementStatus(
                AdvertisementStatus.INACTIVE
        );
        advertisement.setUpdatedAt(OffsetDateTime.now());

        advertisementRepository.update(advertisement);
    }

    @Override
    @Transactional
    public void block(Long advertisementId) {

        User currentUser = currentUserProvider.getCurrentUser();

        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new AdvertisementAccessDeniedException(
                    advertisementId
            );
        }

        Advertisement advertisement = getAdvertisement(advertisementId);

        if (advertisement.getAdvertisementStatus()
                != AdvertisementStatus.ACTIVE
                && advertisement.getAdvertisementStatus()
                != AdvertisementStatus.INACTIVE) {
            throw new InvalidAdvertisementStatusException();
        }

        advertisement.setAdvertisementStatus(
                AdvertisementStatus.BLOCKED
        );
        advertisement.setUpdatedAt(OffsetDateTime.now());

        advertisementRepository.update(advertisement);
    }

    @Override
    @Transactional
    public void unblock(Long advertisementId) {

        User currentUser = currentUserProvider.getCurrentUser();

        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new AdvertisementAccessDeniedException(
                    advertisementId
            );
        }

        Advertisement advertisement = getAdvertisement(advertisementId);

        if (advertisement.getAdvertisementStatus()
                != AdvertisementStatus.BLOCKED) {
            throw new InvalidAdvertisementStatusException();
        }

        advertisement.setAdvertisementStatus(
                AdvertisementStatus.INACTIVE
        );
        advertisement.setUpdatedAt(OffsetDateTime.now());

        advertisementRepository.update(advertisement);
    }

    @Override
    @Transactional
    public void delete(Long advertisementId) {

        Advertisement advertisement = getAdvertisement(advertisementId);

        checkOwner(advertisement);

        if (advertisement.getAdvertisementStatus()
                != AdvertisementStatus.ACTIVE
                && advertisement.getAdvertisementStatus()
                != AdvertisementStatus.INACTIVE) {
            throw new InvalidAdvertisementStatusException();
        }

        advertisement.setAdvertisementStatus(
                AdvertisementStatus.DELETED
        );
        advertisement.setUpdatedAt(OffsetDateTime.now());

        advertisementRepository.update(advertisement);
    }

    private Advertisement getAdvertisement(Long advertisementId) {
        return advertisementRepository.findById(advertisementId)
                .orElseThrow(() ->
                        new AdvertisementNotFoundException(advertisementId));
    }

    private void checkOwner(Advertisement advertisement) {

        User currentUser = currentUserProvider.getCurrentUser();

        if (!advertisement.getSeller().getId().equals(currentUser.getId())) {
            throw new AdvertisementAccessDeniedException(advertisement.getId());
        }
    }
}