package ru.drmemex.classifieds.feature.advertisement.image.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.drmemex.classifieds.feature.advertisement.entity.Advertisement;
import ru.drmemex.classifieds.feature.advertisement.exception.AdvertisementAccessDeniedException;
import ru.drmemex.classifieds.feature.advertisement.exception.AdvertisementNotFoundException;
import ru.drmemex.classifieds.feature.advertisement.image.dto.request.AdvertisementImageRequest;
import ru.drmemex.classifieds.feature.advertisement.image.dto.response.AdvertisementImageResponse;
import ru.drmemex.classifieds.feature.advertisement.image.entity.AdvertisementImage;
import ru.drmemex.classifieds.feature.advertisement.image.exception.AdvertisementImageAccessDeniedException;
import ru.drmemex.classifieds.feature.advertisement.image.exception.AdvertisementImageInvalidDisplayOrderException;
import ru.drmemex.classifieds.feature.advertisement.image.exception.AdvertisementImageLimitExceededException;
import ru.drmemex.classifieds.feature.advertisement.image.exception.AdvertisementImageNotFoundException;
import ru.drmemex.classifieds.feature.advertisement.image.mapper.AdvertisementImageMapper;
import ru.drmemex.classifieds.feature.advertisement.image.repository.AdvertisementImageRepository;
import ru.drmemex.classifieds.feature.advertisement.image.service.AdvertisementImageService;
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
import ru.drmemex.classifieds.feature.advertisement.repository.AdvertisementRepository;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.feature.user.model.UserStatus;
import ru.drmemex.classifieds.feature.user.repository.UserRepository;
import ru.drmemex.classifieds.security.exception.InvalidCredentialsException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdvertisementImageServiceImpl implements AdvertisementImageService {

    private static final int MAX_IMAGES = 5;

    private final AdvertisementImageRepository advertisementImageRepository;
    private final AdvertisementRepository advertisementRepository;
    private final UserRepository userRepository;
    private final AdvertisementImageMapper advertisementImageMapper;

    @Override
    @Transactional
    public AdvertisementImageResponse create(
            Long advertisementId,
            AdvertisementImageRequest request
    ) {
        Advertisement advertisement = getAdvertisement(advertisementId);

        checkOwner(advertisement);

        long imageCount =
                advertisementImageRepository.countByAdvertisementId(
                        advertisementId
                );

        if (imageCount >= MAX_IMAGES) {
            throw new AdvertisementImageLimitExceededException();
        }

        AdvertisementImage image =
                advertisementImageMapper.toEntity(request);

        image.setAdvertisement(advertisement);
        image.setDisplayOrder((short) (imageCount + 1));

        return advertisementImageMapper.toResponse(
                advertisementImageRepository.save(image)
        );
    }

    @Override
    @Transactional
    public AdvertisementImageResponse reorder(
            Long advertisementId,
            Long imageId,
            Short displayOrder
    ) {
        Advertisement advertisement = getAdvertisement(advertisementId);

        checkOwner(advertisement);

        AdvertisementImage image = getImage(imageId);

        checkImageBelongsToAdvertisement(
                advertisementId,
                image
        );

        List<AdvertisementImage> images =
                advertisementImageRepository.findByAdvertisementId(
                        advertisementId
                );

        if (displayOrder < 1 || displayOrder > images.size()) {
            throw new AdvertisementImageInvalidDisplayOrderException(
                    displayOrder
            );
        }

        if (image.getDisplayOrder().equals(displayOrder)) {
            return advertisementImageMapper.toResponse(image);
        }

        images.remove(image);
        images.add(displayOrder - 1, image);

        reorderImages(images);

        return advertisementImageMapper.toResponse(image);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdvertisementImageResponse> getByAdvertisementId(
            Long advertisementId
    ) {
        Advertisement advertisement = getAdvertisement(advertisementId);

        User currentUser = getCurrentUser();

        boolean isOwner =
                advertisement.getSeller().getId().equals(currentUser.getId());

        if (!isOwner && advertisement.getAdvertisementStatus() != AdvertisementStatus.ACTIVE) {
            throw new AdvertisementAccessDeniedException(
                    advertisementId
            );
        }

        return advertisementImageRepository
                .findByAdvertisementId(advertisementId)
                .stream()
                .map(advertisementImageMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdvertisementImageResponse> getByAdvertisementIdForAdmin(
            Long advertisementId
    ) {
        getAdvertisement(advertisementId);

        return advertisementImageRepository
                .findByAdvertisementId(advertisementId)
                .stream()
                .map(advertisementImageMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(
            Long advertisementId,
            Long imageId
    ) {
        Advertisement advertisement = getAdvertisement(advertisementId);

        checkOwner(advertisement);

        AdvertisementImage image = getImage(imageId);

        checkImageBelongsToAdvertisement(
                advertisementId,
                image
        );

        advertisementImageRepository.delete(image);

        List<AdvertisementImage> images =
                advertisementImageRepository.findByAdvertisementId(
                        advertisementId
                );

        reorderImages(images);
    }

    private void reorderImages(List<AdvertisementImage> images) {
        for (int i = 0; i < images.size(); i++) {
            images.get(i).setDisplayOrder(
                    (short) (i + 1)
            );
        }
    }

    private Advertisement getAdvertisement(Long advertisementId) {
        return advertisementRepository.findById(advertisementId)
                .orElseThrow(() ->
                        new AdvertisementNotFoundException(advertisementId));
    }

    private AdvertisementImage getImage(Long imageId) {
        return advertisementImageRepository.findById(imageId)
                .orElseThrow(() ->
                        new AdvertisementImageNotFoundException(imageId));
    }

    private void checkImageBelongsToAdvertisement(
            Long advertisementId,
            AdvertisementImage image
    ) {
        if (!image.getAdvertisement().getId().equals(advertisementId)) {
            throw new AdvertisementImageAccessDeniedException(
                    image.getId()
            );
        }
    }

    private void checkOwner(Advertisement advertisement) {
        User currentUser = getCurrentUser();

        if (!advertisement.getSeller().getId().equals(currentUser.getId())) {
            throw new AdvertisementAccessDeniedException(
                    advertisement.getId()
            );
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        return userRepository.findByLoginAndStatus(
                authentication.getName(),
                UserStatus.ACTIVE
        ).orElseThrow(InvalidCredentialsException::new);
    }
}