package dev.steveboyer.notesvault.user;

import java.util.Collection;
import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@NullMarked
public class CustomUserDetails implements UserDetails {

  private final Long id;
  private final String username;
  private final String password;

  public CustomUserDetails(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.password = user.getPasswordHash();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  public Long getId() {
    return id;
  }
}
