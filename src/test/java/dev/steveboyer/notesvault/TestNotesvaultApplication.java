package dev.steveboyer.notesvault;

import org.springframework.boot.SpringApplication;

public class TestNotesvaultApplication {

  public static void main(String[] args) {
    SpringApplication.from(NotesvaultApplication::main)
        .with(TestcontainersConfiguration.class)
        .run(args);
  }
}
