package ru.drmemex.classifieds.feature.advertisement.exception;

import ru.drmemex.classifieds.common.exception.ConflictException;

public class AdvertisementRateLimitExceededException extends ConflictException {

    public AdvertisementRateLimitExceededException() {
        super("Advertisement rate limit exceeded");
    }
}