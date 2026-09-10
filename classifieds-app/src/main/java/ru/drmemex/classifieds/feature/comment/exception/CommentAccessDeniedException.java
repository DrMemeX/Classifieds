package ru.drmemex.classifieds.feature.comment.exception;

public class CommentAccessDeniedException extends RuntimeException {
  public CommentAccessDeniedException(String message) {
    super(message);
  }
}
