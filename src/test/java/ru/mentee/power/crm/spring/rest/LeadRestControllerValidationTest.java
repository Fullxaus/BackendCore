package ru.mentee.power.crm.spring.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.mentee.power.crm.spring.dto.CreateLeadRequest;

/**
 * Тесты валидации REST API (Given-When-Then): 400 при невалидных данных, 201 при валидных. JUnit 5,
 * AssertJ, MockMvc.
 */
@SpringBootTest
@ActiveProfiles("test")
class LeadRestControllerValidationTest {

  @Autowired private WebApplicationContext context;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private MockMvc mockMvc() {
    return MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  void shouldReturn400_whenEmailIsBlank() throws Exception {
    // Given: CreateLeadRequest с пустым email
    CreateLeadRequest request = new CreateLeadRequest();
    request.setEmail("");
    request.setFirstName("John");
    request.setLastName("Doe");
    request.setCompany("Acme");
    String requestJson = objectMapper.writeValueAsString(request);

    // When: POST /api/leads
    ResultActions result =
        mockMvc()
            .perform(
                post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson));

    // Then: 400 Bad Request (MethodArgumentNotValidException)
    assertThat(result.andReturn().getResponse().getStatus()).isEqualTo(400);
  }

  @Test
  void shouldReturn400_whenEmailIsInvalidFormat() throws Exception {
    // Given: email в невалидном формате (без @)
    CreateLeadRequest request = new CreateLeadRequest();
    request.setEmail("not-an-email");
    request.setFirstName("John");
    request.setLastName("Doe");
    request.setCompany("Acme");
    String requestJson = objectMapper.writeValueAsString(request);

    // When: POST /api/leads
    ResultActions result =
        mockMvc()
            .perform(
                post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson));

    // Then: 400 Bad Request
    assertThat(result.andReturn().getResponse().getStatus()).isEqualTo(400);
  }

  @Test
  void shouldReturn400_whenFirstNameIsTooShort() throws Exception {
    // Given: firstName из 1 символа (требуется min = 2)
    CreateLeadRequest request = new CreateLeadRequest();
    request.setEmail("valid@example.com");
    request.setFirstName("A");
    request.setLastName("Doe");
    request.setCompany("Acme");
    String requestJson = objectMapper.writeValueAsString(request);

    // When: POST /api/leads
    ResultActions result =
        mockMvc()
            .perform(
                post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson));

    // Then: 400 Bad Request
    assertThat(result.andReturn().getResponse().getStatus()).isEqualTo(400);
  }

  @Test
  void shouldReturn201_whenAllFieldsAreValid() throws Exception {
    // Given: валидный CreateLeadRequest со всеми корректными полями
    CreateLeadRequest request = new CreateLeadRequest();
    request.setEmail("validation-valid@example.com");
    request.setFirstName("John");
    request.setLastName("Doe");
    request.setCompany("Acme");
    String requestJson = objectMapper.writeValueAsString(request);

    // When: POST /api/leads
    ResultActions result =
        mockMvc()
            .perform(
                post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(requestJson));

    // Then: 201 Created с сохранённым Lead
    assertThat(result.andReturn().getResponse().getStatus()).isEqualTo(201);
  }

  @Test
  void shouldReturn400_whenGetLeadByIdWithInvalidUuid() throws Exception {
    // Given: path variable "null" (невалидный UUID)
    // When: GET /api/leads/null
    ResultActions result = mockMvc().perform(get("/api/leads/null"));

    // Then: 400 Bad Request (ConstraintViolationException или conversion error)
    assertThat(result.andReturn().getResponse().getStatus()).isEqualTo(400);
  }
}
