package dev.steveboyer.notesvault.auth;

import dev.steveboyer.notesvault.auth.dto.LoginRequest;
import dev.steveboyer.notesvault.auth.dto.RegisterRequest;
import dev.steveboyer.notesvault.common.exception.UserAlreadyExistsException;
import dev.steveboyer.notesvault.security.JwtService;
import dev.steveboyer.notesvault.user.CustomUserDetails;
import dev.steveboyer.notesvault.user.User;
import dev.steveboyer.notesvault.user.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(
      UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public User register(RegisterRequest registerRequest) {
    if (userRepository.existsByUsername(registerRequest.username())) {
      throw new UserAlreadyExistsException("Username already taken");
    }

    String hash = passwordEncoder.encode(registerRequest.password());

    User user = new User();
    user.setUsername(registerRequest.username());
    user.setPasswordHash(hash);

    return userRepository.save(user);
  }

  public String login(LoginRequest loginRequest) {
    User user =
        userRepository
            .findByUsername(loginRequest.username())
            .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

    if (!passwordEncoder.matches(loginRequest.password(), user.getPasswordHash())) {
      throw new BadCredentialsException("Invalid username or password");
    }

    CustomUserDetails customUserDetails = new CustomUserDetails(user);
    return jwtService.generateToken(customUserDetails);
  }
}
