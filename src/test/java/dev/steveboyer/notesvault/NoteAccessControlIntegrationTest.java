package dev.steveboyer.notesvault;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import dev.steveboyer.notesvault.auth.dto.AuthResponse;
import dev.steveboyer.notesvault.auth.dto.LoginRequest;
import dev.steveboyer.notesvault.auth.dto.RegisterRequest;
import dev.steveboyer.notesvault.note.dto.CreateNoteRequest;
import dev.steveboyer.notesvault.note.dto.NoteResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public class NoteAccessControlIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  public void testUserCannotAccessAnotherUsersNote() throws Exception {
    // Create user1 (username1) and login
    String tokenUser1 = registerAndLogin("username1", "password1");

    // Create user2 (username2) and login
    String tokenUser2 = registerAndLogin("username2", "password2");

    // Create a note as user1 (username1)
    String notesResponse =
        mockMvc
            .perform(
                post("/notes")
                    .header("Authorization", "Bearer " + tokenUser1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new CreateNoteRequest("some string"))))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    // Get the id for user1's note from the response
    Long noteId = objectMapper.readValue(notesResponse, NoteResponse.class).id();

    // As user2, try to retrieve user1's note. 404 (NOT FOUND) status code is returned
    mockMvc
        .perform(get("/notes/" + noteId).header("Authorization", "Bearer " + tokenUser2))
        .andExpect(status().isNotFound());
  }

  private String registerAndLogin(String username, String password) throws Exception {
    mockMvc
        .perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RegisterRequest(username, password))))
        .andExpect(status().isCreated());

    String body =
        mockMvc
            .perform(
                post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new LoginRequest(username, password))))
            .andReturn()
            .getResponse()
            .getContentAsString();

    return objectMapper.readValue(body, AuthResponse.class).token();
  }
}
