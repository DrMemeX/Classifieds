package ru.drmemex.classifieds.feature.comment.exception;

public class CommentCreationNotAllowedException extends RuntimeException {
  public CommentCreationNotAllowedException(String message) {
    super(message);
  }
}
