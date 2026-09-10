package ru.drmemex.classifieds.feature.conversation.message.exception;

public class MessageRateLimitExceededException extends RuntimeException {
  public MessageRateLimitExceededException(String message) {
    super(message);
  }
}
