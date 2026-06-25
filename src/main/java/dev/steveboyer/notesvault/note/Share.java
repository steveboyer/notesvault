package dev.steveboyer.notesvault.note;

import dev.steveboyer.notesvault.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "shares", uniqueConstraints = @UniqueConstraint(columnNames = {"note_id", "user_id"}))
public class Share {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "note_id", nullable = false)
  private Note note;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User sharedWithUser;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  public Share() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Note getNote() {
    return note;
  }

  public void setNote(Note note) {
    this.note = note;
  }

  public User getSharedWithUser() {
    return sharedWithUser;
  }

  public void setSharedWithUser(User sharedWithUser) {
    this.sharedWithUser = sharedWithUser;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
