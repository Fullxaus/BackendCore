package ru.mentee.power.crm.spring.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Тесты GlobalExceptionHandler: 400, 404, 409 через реальный контекст; 500 — через тестовую
 * конфигурацию.
 */
@SpringBootTest
@ActiveProfiles("test")
public class GlobalExceptionHandlerTest {

  @Autowired private WebApplicationContext context;

  private MockMvc mockMvc() {
    return MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  void shouldReturn404_whenEntityNotFound() throws Exception {
    UUID id = UUID.randomUUID();

    mockMvc()
        .perform(get("/api/leads/{id}", id))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Lead not found with id: " + id))
        .andExpect(jsonPath("$.path").value("/api/leads/" + id));
  }

  @Test
  void shouldReturn400WithFieldErrors_whenValidationFails() throws Exception {
    String body = "{\"email\": \"\", \"firstName\": \"\"}";

    mockMvc()
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.errors").exists())
        .andExpect(jsonPath("$.errors.email").exists())
        .andExpect(jsonPath("$.errors.firstName").exists())
        .andExpect(jsonPath("$.path").value("/api/leads"));
  }

  @Test
  void shouldReturn409_whenDuplicateEmail() throws Exception {
    String body =
        "{\"email\":\"dup-409@example.com\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"company\":\"Acme\"}";

    mockMvc()
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isCreated());

    mockMvc()
        .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(body))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.error").value("Conflict"))
        .andExpect(
            jsonPath("$.message").value("Lead with email already exists: dup-409@example.com"))
        .andExpect(jsonPath("$.path").value("/api/leads"));
  }
}
