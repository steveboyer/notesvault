package dev.steveboyer.notesvault.note;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
  List<Note> findByOwner_Id(Long ownerId);

  Optional<Note> findByOwner_IdAndId(Long ownerId, Long id);
}
