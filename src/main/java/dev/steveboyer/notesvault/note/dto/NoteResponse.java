package dev.steveboyer.notesvault.note.dto;

import java.time.Instant;

public record NoteResponse(
    Long id, String content, String userName, Instant createdAt, Instant updatedAt) {}
