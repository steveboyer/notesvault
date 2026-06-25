package dev.steveboyer.notesvault.common.exception;

public class NoteAlreadySharedException extends RuntimeException {
  public NoteAlreadySharedException(String message) {
    super(message);
  }
}
