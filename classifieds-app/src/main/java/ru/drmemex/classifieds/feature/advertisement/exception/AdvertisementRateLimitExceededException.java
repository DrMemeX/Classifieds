package ru.drmemex.classifieds.feature.advertisement.exception;

public class AdvertisementRateLimitExceededException extends RuntimeException {
  public AdvertisementRateLimitExceededException(String message) {
    super(message);
  }
}
