package ru.drmemex.classifieds.feature.comment.exception;

public class CommentRateLimitExceededException extends RuntimeException {
  public CommentRateLimitExceededException(String message) {
    super(message);
  }
}
