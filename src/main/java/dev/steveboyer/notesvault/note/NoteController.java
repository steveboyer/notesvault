package dev.steveboyer.notesvault.note;

import dev.steveboyer.notesvault.note.dto.CreateNoteRequest;
import dev.steveboyer.notesvault.note.dto.NoteResponse;
import dev.steveboyer.notesvault.note.dto.ShareNoteRequest;
import dev.steveboyer.notesvault.note.dto.UpdateNoteRequest;
import dev.steveboyer.notesvault.user.CustomUserDetails;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notes")
public class NoteController {
  private final NoteService noteService;

  public NoteController(NoteService noteService) {
    this.noteService = noteService;
  }

  @GetMapping
  public ResponseEntity<List<NoteResponse>> getAllNotes(
      @AuthenticationPrincipal CustomUserDetails principal) {
    return ResponseEntity.ok(noteService.findByOwnerId(principal.getId()));
  }

  @PostMapping
  public ResponseEntity<NoteResponse> createNote(
      @Valid @RequestBody CreateNoteRequest request,
      @AuthenticationPrincipal CustomUserDetails principal) {
    NoteResponse created = noteService.createNote(principal.getId(), request.content());
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping("/{id}")
  public ResponseEntity<NoteResponse> getNote(
      @AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long id) {
    return ResponseEntity.ok(noteService.getNote(principal.getId(), id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteNote(
      @AuthenticationPrincipal CustomUserDetails principal, @PathVariable Long id) {
    noteService.deleteNote(principal.getId(), id);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{id}/share")
  public ResponseEntity<Void> shareNote(
      @AuthenticationPrincipal CustomUserDetails principal,
      @PathVariable Long id,
      @Valid @RequestBody ShareNoteRequest request) {
    noteService.shareNote(principal.getId(), id, request.shareUserId());
    return ResponseEntity.ok().build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<NoteResponse> updateNote(
      @PathVariable Long id,
      @Valid @RequestBody UpdateNoteRequest request,
      @AuthenticationPrincipal CustomUserDetails principal) {
    NoteResponse updated = noteService.updateNote(principal.getId(), id, request.content());
    return ResponseEntity.ok(updated);
  }
}
