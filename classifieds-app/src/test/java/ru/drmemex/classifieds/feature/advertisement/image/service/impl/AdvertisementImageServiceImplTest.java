package ru.drmemex.classifieds.feature.advertisement.image.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import ru.drmemex.classifieds.feature.advertisement.model.AdvertisementStatus;
import ru.drmemex.classifieds.feature.advertisement.repository.AdvertisementRepository;
import ru.drmemex.classifieds.feature.user.entity.User;
import ru.drmemex.classifieds.security.provider.CurrentUserProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdvertisementImageServiceImplTest {

    @Mock
    private AdvertisementImageRepository advertisementImageRepository;

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock
    private AdvertisementImageMapper advertisementImageMapper;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private AdvertisementImageServiceImpl advertisementImageService;

    @Test
    void create_ShouldCreateImage() {

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);

        AdvertisementImageRequest request =
                mock(AdvertisementImageRequest.class);

        AdvertisementImage image =
                new AdvertisementImage();

        AdvertisementImage savedImage =
                new AdvertisementImage();
        savedImage.setId(1L);

        AdvertisementImageResponse expectedResponse =
                mock(AdvertisementImageResponse.class);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        when(advertisementImageRepository.countByAdvertisementId(1L))
                .thenReturn(4L);

        when(advertisementImageMapper.toEntity(request))
                .thenReturn(image);

        when(advertisementImageRepository.save(image))
                .thenReturn(savedImage);

        when(advertisementImageMapper.toResponse(savedImage))
                .thenReturn(expectedResponse);

        AdvertisementImageResponse result =
                advertisementImageService.create(
                        1L,
                        request
                );

        assertEquals(
                expectedResponse,
                result
        );

        assertEquals(
                advertisement,
                image.getAdvertisement()
        );

        assertEquals(
                (short) 5,
                image.getDisplayOrder()
        );

        verify(advertisementImageRepository)
                .save(image);
    }

    @Test
    void create_ShouldThrowException_WhenAdvertisementNotFound() {

        AdvertisementImageRequest request =
                mock(AdvertisementImageRequest.class);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                AdvertisementNotFoundException.class,
                () -> advertisementImageService.create(
                        1L,
                        request
                )
        );

        verify(currentUserProvider, never())
                .getCurrentUser();

        verify(advertisementImageRepository, never())
                .save(any(AdvertisementImage.class));
    }

    @Test
    void create_ShouldThrowException_WhenUserIsNotOwner() {

        User seller = new User();
        seller.setId(1L);

        User currentUser = new User();
        currentUser.setId(2L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);

        AdvertisementImageRequest request =
                mock(AdvertisementImageRequest.class);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        assertThrows(
                AdvertisementAccessDeniedException.class,
                () -> advertisementImageService.create(
                        1L,
                        request
                )
        );

        verify(advertisementImageRepository, never())
                .countByAdvertisementId(1L);

        verify(advertisementImageRepository, never())
                .save(any(AdvertisementImage.class));
    }

    @Test
    void create_ShouldThrowException_WhenImageLimitExceeded() {

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);

        AdvertisementImageRequest request =
                mock(AdvertisementImageRequest.class);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        when(advertisementImageRepository.countByAdvertisementId(1L))
                .thenReturn(5L);

        assertThrows(
                AdvertisementImageLimitExceededException.class,
                () -> advertisementImageService.create(
                        1L,
                        request
                )
        );

        verify(advertisementImageMapper, never())
                .toEntity(any(AdvertisementImageRequest.class));

        verify(advertisementImageRepository, never())
                .save(any(AdvertisementImage.class));
    }

    @Test
    void reorder_ShouldReorderImage() {

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);

        AdvertisementImage firstImage = new AdvertisementImage();
        firstImage.setId(1L);
        firstImage.setAdvertisement(advertisement);
        firstImage.setDisplayOrder((short) 1);

        AdvertisementImage secondImage = new AdvertisementImage();
        secondImage.setId(2L);
        secondImage.setAdvertisement(advertisement);
        secondImage.setDisplayOrder((short) 2);

        AdvertisementImage thirdImage = new AdvertisementImage();
        thirdImage.setId(3L);
        thirdImage.setAdvertisement(advertisement);
        thirdImage.setDisplayOrder((short) 3);

        AdvertisementImageResponse expectedResponse =
                mock(AdvertisementImageResponse.class);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        when(advertisementImageRepository.findById(3L))
                .thenReturn(Optional.of(thirdImage));

        when(advertisementImageRepository.findByAdvertisementId(1L))
                .thenReturn(
                        new ArrayList<>(
                                List.of(
                                        firstImage,
                                        secondImage,
                                        thirdImage
                                )
                        )
                );

        when(advertisementImageMapper.toResponse(thirdImage))
                .thenReturn(expectedResponse);

        AdvertisementImageResponse result =
                advertisementImageService.reorder(
                        1L,
                        3L,
                        (short) 1
                );

        assertEquals(
                expectedResponse,
                result
        );

        assertEquals(
                (short) 1,
                thirdImage.getDisplayOrder()
        );

        assertEquals(
                (short) 2,
                firstImage.getDisplayOrder()
        );

        assertEquals(
                (short) 3,
                secondImage.getDisplayOrder()
        );

        verify(advertisementImageMapper)
                .toResponse(thirdImage);
    }

    @Test
    void reorder_ShouldReturnImage_WhenDisplayOrderIsUnchanged() {

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);

        AdvertisementImage firstImage = new AdvertisementImage();
        firstImage.setId(1L);
        firstImage.setAdvertisement(advertisement);
        firstImage.setDisplayOrder((short) 1);

        AdvertisementImage secondImage = new AdvertisementImage();
        secondImage.setId(2L);
        secondImage.setAdvertisement(advertisement);
        secondImage.setDisplayOrder((short) 2);

        AdvertisementImage thirdImage = new AdvertisementImage();
        thirdImage.setId(3L);
        thirdImage.setAdvertisement(advertisement);
        thirdImage.setDisplayOrder((short) 3);

        AdvertisementImageResponse expectedResponse =
                mock(AdvertisementImageResponse.class);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        when(advertisementImageRepository.findById(2L))
                .thenReturn(Optional.of(secondImage));

        when(advertisementImageRepository.findByAdvertisementId(1L))
                .thenReturn(
                        List.of(
                                firstImage,
                                secondImage,
                                thirdImage
                        )
                );

        when(advertisementImageMapper.toResponse(secondImage))
                .thenReturn(expectedResponse);

        AdvertisementImageResponse result =
                advertisementImageService.reorder(
                        1L,
                        2L,
                        (short) 2
                );

        assertEquals(
                expectedResponse,
                result
        );

        assertEquals(
                (short) 1,
                firstImage.getDisplayOrder()
        );

        assertEquals(
                (short) 2,
                secondImage.getDisplayOrder()
        );

        assertEquals(
                (short) 3,
                thirdImage.getDisplayOrder()
        );

        verify(advertisementImageMapper)
                .toResponse(secondImage);
    }

    @Test
    void reorder_ShouldThrowException_WhenAdvertisementNotFound() {

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                AdvertisementNotFoundException.class,
                () -> advertisementImageService.reorder(
                        1L,
                        1L,
                        (short) 1
                )
        );

        verify(currentUserProvider, never())
                .getCurrentUser();

        verify(advertisementImageRepository, never())
                .findById(any());

        verify(advertisementImageRepository, never())
                .findByAdvertisementId(any());
    }

    @Test
    void reorder_ShouldThrowException_WhenUserIsNotOwner() {

        User seller = new User();
        seller.setId(1L);

        User currentUser = new User();
        currentUser.setId(2L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        assertThrows(
                AdvertisementAccessDeniedException.class,
                () -> advertisementImageService.reorder(
                        1L,
                        1L,
                        (short) 1
                )
        );

        verify(advertisementImageRepository, never())
                .findById(any());

        verify(advertisementImageRepository, never())
                .findByAdvertisementId(any());
    }

    @Test
    void reorder_ShouldThrowException_WhenImageNotFound() {

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        when(advertisementImageRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(
                AdvertisementImageNotFoundException.class,
                () -> advertisementImageService.reorder(
                        1L,
                        2L,
                        (short) 1
                )
        );

        verify(advertisementImageRepository, never())
                .findByAdvertisementId(any());
    }

    @Test
    void reorder_ShouldThrowException_WhenImageBelongsToAnotherAdvertisement() {

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);

        Advertisement anotherAdvertisement = new Advertisement();
        anotherAdvertisement.setId(2L);

        AdvertisementImage image = new AdvertisementImage();
        image.setId(3L);
        image.setAdvertisement(anotherAdvertisement);
        image.setDisplayOrder((short) 1);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        when(advertisementImageRepository.findById(3L))
                .thenReturn(Optional.of(image));

        assertThrows(
                AdvertisementImageAccessDeniedException.class,
                () -> advertisementImageService.reorder(
                        1L,
                        3L,
                        (short) 1
                )
        );

        verify(advertisementImageRepository, never())
                .findByAdvertisementId(any());
    }

    @Test
    void reorder_ShouldThrowException_WhenDisplayOrderIsLessThanOne() {

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);

        AdvertisementImage image = new AdvertisementImage();
        image.setId(1L);
        image.setAdvertisement(advertisement);
        image.setDisplayOrder((short) 1);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        when(advertisementImageRepository.findById(1L))
                .thenReturn(Optional.of(image));

        when(advertisementImageRepository.findByAdvertisementId(1L))
                .thenReturn(List.of(image));

        assertThrows(
                AdvertisementImageInvalidDisplayOrderException.class,
                () -> advertisementImageService.reorder(
                        1L,
                        1L,
                        (short) 0
                )
        );

        assertEquals(
                (short) 1,
                image.getDisplayOrder()
        );
    }

    @Test
    void reorder_ShouldThrowException_WhenDisplayOrderExceedsImageCount() {

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);

        AdvertisementImage firstImage = new AdvertisementImage();
        firstImage.setId(1L);
        firstImage.setAdvertisement(advertisement);
        firstImage.setDisplayOrder((short) 1);

        AdvertisementImage secondImage = new AdvertisementImage();
        secondImage.setId(2L);
        secondImage.setAdvertisement(advertisement);
        secondImage.setDisplayOrder((short) 2);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        when(advertisementImageRepository.findById(2L))
                .thenReturn(Optional.of(secondImage));

        when(advertisementImageRepository.findByAdvertisementId(1L))
                .thenReturn(
                        List.of(
                                firstImage,
                                secondImage
                        )
                );

        assertThrows(
                AdvertisementImageInvalidDisplayOrderException.class,
                () -> advertisementImageService.reorder(
                        1L,
                        2L,
                        (short) 3
                )
        );

        assertEquals(
                (short) 2,
                secondImage.getDisplayOrder()
        );
    }

    @Test
    void getByAdvertisementId_ShouldReturnImages_WhenUserIsOwner() {

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);
        advertisement.setAdvertisementStatus(
                AdvertisementStatus.INACTIVE
        );

        AdvertisementImage firstImage = new AdvertisementImage();
        firstImage.setId(1L);
        firstImage.setAdvertisement(advertisement);
        firstImage.setDisplayOrder((short) 1);

        AdvertisementImage secondImage = new AdvertisementImage();
        secondImage.setId(2L);
        secondImage.setAdvertisement(advertisement);
        secondImage.setDisplayOrder((short) 2);

        AdvertisementImageResponse firstResponse =
                mock(AdvertisementImageResponse.class);

        AdvertisementImageResponse secondResponse =
                mock(AdvertisementImageResponse.class);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        when(advertisementImageRepository.findByAdvertisementId(1L))
                .thenReturn(
                        List.of(
                                firstImage,
                                secondImage
                        )
                );

        when(advertisementImageMapper.toResponse(firstImage))
                .thenReturn(firstResponse);

        when(advertisementImageMapper.toResponse(secondImage))
                .thenReturn(secondResponse);

        List<AdvertisementImageResponse> result =
                advertisementImageService.getByAdvertisementId(1L);

        assertEquals(
                List.of(
                        firstResponse,
                        secondResponse
                ),
                result
        );

        verify(advertisementImageRepository)
                .findByAdvertisementId(1L);
    }

    @Test
    void getByAdvertisementId_ShouldReturnImages_WhenUserIsNotOwnerAndAdvertisementIsActive() {

        User seller = new User();
        seller.setId(1L);

        User currentUser = new User();
        currentUser.setId(2L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);
        advertisement.setAdvertisementStatus(
                AdvertisementStatus.ACTIVE
        );

        AdvertisementImage image = new AdvertisementImage();
        image.setId(1L);
        image.setAdvertisement(advertisement);
        image.setDisplayOrder((short) 1);

        AdvertisementImageResponse response =
                mock(AdvertisementImageResponse.class);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        when(advertisementImageRepository.findByAdvertisementId(1L))
                .thenReturn(List.of(image));

        when(advertisementImageMapper.toResponse(image))
                .thenReturn(response);

        List<AdvertisementImageResponse> result =
                advertisementImageService.getByAdvertisementId(1L);

        assertEquals(
                List.of(response),
                result
        );

        verify(advertisementImageRepository)
                .findByAdvertisementId(1L);
    }

    @Test
    void getByAdvertisementId_ShouldThrowException_WhenUserIsNotOwnerAndAdvertisementIsNotActive() {

        User seller = new User();
        seller.setId(1L);

        User currentUser = new User();
        currentUser.setId(2L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);
        advertisement.setAdvertisementStatus(
                AdvertisementStatus.INACTIVE
        );

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        assertThrows(
                AdvertisementAccessDeniedException.class,
                () -> advertisementImageService
                        .getByAdvertisementId(1L)
        );

        verify(advertisementImageRepository, never())
                .findByAdvertisementId(any());
    }

    @Test
    void getByAdvertisementId_ShouldThrowException_WhenAdvertisementNotFound() {

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                AdvertisementNotFoundException.class,
                () -> advertisementImageService
                        .getByAdvertisementId(1L)
        );

        verify(currentUserProvider, never())
                .getCurrentUser();

        verify(advertisementImageRepository, never())
                .findByAdvertisementId(any());
    }

    @Test
    void getByAdvertisementIdForAdmin_ShouldReturnImages() {

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);

        AdvertisementImage firstImage = new AdvertisementImage();
        firstImage.setId(1L);
        firstImage.setAdvertisement(advertisement);
        firstImage.setDisplayOrder((short) 1);

        AdvertisementImage secondImage = new AdvertisementImage();
        secondImage.setId(2L);
        secondImage.setAdvertisement(advertisement);
        secondImage.setDisplayOrder((short) 2);

        AdvertisementImageResponse firstResponse =
                mock(AdvertisementImageResponse.class);

        AdvertisementImageResponse secondResponse =
                mock(AdvertisementImageResponse.class);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(advertisementImageRepository.findByAdvertisementId(1L))
                .thenReturn(
                        List.of(
                                firstImage,
                                secondImage
                        )
                );

        when(advertisementImageMapper.toResponse(firstImage))
                .thenReturn(firstResponse);

        when(advertisementImageMapper.toResponse(secondImage))
                .thenReturn(secondResponse);

        List<AdvertisementImageResponse> result =
                advertisementImageService
                        .getByAdvertisementIdForAdmin(1L);

        assertEquals(
                List.of(
                        firstResponse,
                        secondResponse
                ),
                result
        );

        verify(advertisementRepository)
                .findById(1L);

        verify(advertisementImageRepository)
                .findByAdvertisementId(1L);
    }

    @Test
    void getByAdvertisementIdForAdmin_ShouldThrowException_WhenAdvertisementNotFound() {

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                AdvertisementNotFoundException.class,
                () -> advertisementImageService
                        .getByAdvertisementIdForAdmin(1L)
        );

        verify(advertisementImageRepository, never())
                .findByAdvertisementId(any());

        verify(advertisementImageMapper, never())
                .toResponse(any(AdvertisementImage.class));
    }

    @Test
    void delete_ShouldDeleteImageAndReorderRemainingImages() {

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);

        AdvertisementImage firstImage = new AdvertisementImage();
        firstImage.setId(1L);
        firstImage.setAdvertisement(advertisement);
        firstImage.setDisplayOrder((short) 1);

        AdvertisementImage imageToDelete = new AdvertisementImage();
        imageToDelete.setId(2L);
        imageToDelete.setAdvertisement(advertisement);
        imageToDelete.setDisplayOrder((short) 2);

        AdvertisementImage thirdImage = new AdvertisementImage();
        thirdImage.setId(3L);
        thirdImage.setAdvertisement(advertisement);
        thirdImage.setDisplayOrder((short) 3);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        when(advertisementImageRepository.findById(2L))
                .thenReturn(Optional.of(imageToDelete));

        when(advertisementImageRepository.findByAdvertisementId(1L))
                .thenReturn(
                        List.of(
                                firstImage,
                                thirdImage
                        )
                );

        advertisementImageService.delete(
                1L,
                2L
        );

        assertEquals(
                (short) 1,
                firstImage.getDisplayOrder()
        );

        assertEquals(
                (short) 2,
                thirdImage.getDisplayOrder()
        );

        verify(advertisementImageRepository)
                .delete(imageToDelete);

        verify(advertisementImageRepository)
                .findByAdvertisementId(1L);
    }

    @Test
    void delete_ShouldThrowException_WhenAdvertisementNotFound() {

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                AdvertisementNotFoundException.class,
                () -> advertisementImageService.delete(
                        1L,
                        2L
                )
        );

        verify(currentUserProvider, never())
                .getCurrentUser();

        verify(advertisementImageRepository, never())
                .findById(any());

        verify(advertisementImageRepository, never())
                .delete(any(AdvertisementImage.class));
    }

    @Test
    void delete_ShouldThrowException_WhenUserIsNotOwner() {

        User seller = new User();
        seller.setId(1L);

        User currentUser = new User();
        currentUser.setId(2L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(currentUser);

        assertThrows(
                AdvertisementAccessDeniedException.class,
                () -> advertisementImageService.delete(
                        1L,
                        2L
                )
        );

        verify(advertisementImageRepository, never())
                .findById(any());

        verify(advertisementImageRepository, never())
                .delete(any(AdvertisementImage.class));
    }

    @Test
    void delete_ShouldThrowException_WhenImageNotFound() {

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        when(advertisementImageRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(
                AdvertisementImageNotFoundException.class,
                () -> advertisementImageService.delete(
                        1L,
                        2L
                )
        );

        verify(advertisementImageRepository, never())
                .delete(any(AdvertisementImage.class));

        verify(advertisementImageRepository, never())
                .findByAdvertisementId(any());
    }

    @Test
    void delete_ShouldThrowException_WhenImageBelongsToAnotherAdvertisement() {

        User seller = new User();
        seller.setId(1L);

        Advertisement advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setSeller(seller);

        Advertisement anotherAdvertisement = new Advertisement();
        anotherAdvertisement.setId(2L);

        AdvertisementImage image = new AdvertisementImage();
        image.setId(3L);
        image.setAdvertisement(anotherAdvertisement);
        image.setDisplayOrder((short) 1);

        when(advertisementRepository.findById(1L))
                .thenReturn(Optional.of(advertisement));

        when(currentUserProvider.getCurrentUser())
                .thenReturn(seller);

        when(advertisementImageRepository.findById(3L))
                .thenReturn(Optional.of(image));

        assertThrows(
                AdvertisementImageAccessDeniedException.class,
                () -> advertisementImageService.delete(
                        1L,
                        3L
                )
        );

        verify(advertisementImageRepository, never())
                .delete(any(AdvertisementImage.class));

        verify(advertisementImageRepository, never())
                .findByAdvertisementId(any());
    }
}