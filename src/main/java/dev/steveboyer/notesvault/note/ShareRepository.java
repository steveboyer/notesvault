package dev.steveboyer.notesvault.note;

import dev.steveboyer.notesvault.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareRepository extends JpaRepository<Share, Long> {
  boolean existsByNoteAndSharedWithUser(Note note, User user);
}
