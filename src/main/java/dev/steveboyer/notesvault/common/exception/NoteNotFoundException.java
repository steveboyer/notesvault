package dev.steveboyer.notesvault.common.exception;

public class NoteNotFoundException extends RuntimeException {
  public NoteNotFoundException(String message) {
    super(message);
  }
}
