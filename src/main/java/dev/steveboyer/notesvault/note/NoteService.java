package dev.steveboyer.notesvault.note;

import dev.steveboyer.notesvault.common.exception.NoteAlreadySharedException;
import dev.steveboyer.notesvault.common.exception.NoteNotFoundException;
import dev.steveboyer.notesvault.common.exception.UserNotFoundException;
import dev.steveboyer.notesvault.note.dto.NoteResponse;
import dev.steveboyer.notesvault.user.User;
import dev.steveboyer.notesvault.user.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoteService {

  private final NoteRepository noteRepository;
  private final UserRepository userRepository;
  private final ShareRepository shareRepository;

  public NoteService(
      NoteRepository noteRepository,
      UserRepository userRepository,
      ShareRepository shareRepository) {
    this.noteRepository = noteRepository;
    this.userRepository = userRepository;
    this.shareRepository = shareRepository;
  }

  @Transactional
  public NoteResponse createNote(Long userId, String content) {
    User owner =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

    Note note = new Note();
    note.setContent(content);
    note.setOwner(owner);
    noteRepository.save(note);

    return toResponse(note);
  }

  @Transactional(readOnly = true)
  public List<NoteResponse> findByOwnerId(Long id) {
    return noteRepository.findByOwner_Id(id).stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public NoteResponse getNote(Long userId, Long noteId) {
    Note note =
        noteRepository
            .findById(noteId)
            .orElseThrow(() -> new NoteNotFoundException("Note not found"));

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

    boolean isOwner = note.getOwner().getId().equals(userId);
    boolean isSharedWithUser = shareRepository.existsByNoteAndSharedWithUser(note, user);

    if (!isOwner && !isSharedWithUser) {
      throw new NoteNotFoundException("Note not found"); // 404, don't leak existence
    }

    return toResponse(note);
  }

  @Transactional
  public void deleteNote(Long userId, Long noteId) {
    Note note =
        noteRepository
            .findByOwner_IdAndId(userId, noteId)
            .orElseThrow(() -> new NoteNotFoundException("Note not found"));

    noteRepository.delete(note);
  }

  @Transactional
  public NoteResponse updateNote(Long userId, Long noteId, String content) {
    User owner =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

    Note note =
        noteRepository
            .findByOwner_IdAndId(userId, noteId)
            .orElseThrow(() -> new NoteNotFoundException("Note not found"));
    note.setContent(content);
    note.setOwner(owner);
    noteRepository.save(note);
    return toResponse(note);
  }

  @Transactional
  public void shareNote(Long userId, Long noteId, Long shareUserId) {
    Note note =
        noteRepository
            .findByOwner_IdAndId(userId, noteId)
            .orElseThrow(() -> new NoteNotFoundException("Note not found"));
    User shareUser =
        userRepository
            .findById(shareUserId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));

    if (shareRepository.existsByNoteAndSharedWithUser(note, shareUser)) {
      throw new NoteAlreadySharedException("Note already shared");
    }

    Share share = new Share();
    share.setNote(note);
    share.setSharedWithUser(shareUser);
    shareRepository.save(share);
  }

  public NoteResponse toResponse(Note note) {
    return new NoteResponse(
        note.getId(),
        note.getContent(),
        note.getOwner().getUsername(),
        note.getCreatedAt(),
        note.getUpdatedAt());
  }
}
