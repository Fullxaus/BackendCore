package ru.mentee.power.crm.spring.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

/**
 * Контракт-тесты для {@link EmailValidationClient} (RestTemplate): проверка десериализации и
 * поведения при разных ответах внешнего API (MockRestServiceServer).
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "email.validation.base-url=http://localhost:8089")
class EmailValidationClientContractTest {

  private MockRestServiceServer mockServer;

  @Autowired private RestTemplate restTemplate;
  @Autowired private EmailValidationClient emailValidationClient;

  @BeforeEach
  void setUp() {
    mockServer = MockRestServiceServer.bindTo(restTemplate).build();
  }

  @Test
  void shouldReturnValidResponse_whenEmailIsValid() {
    mockServer
        .expect(requestTo(containsString("/api/validate/email")))
        .andRespond(
            withSuccess(
                """
                {
                  "email": "john@example.com",
                  "valid": true,
                  "reason": "Email exists and is deliverable"
                }
                """,
                APPLICATION_JSON));

    EmailValidationResponse response = emailValidationClient.validateEmail("john@example.com");

    assertThat(response).isNotNull();
    assertThat(response.valid()).isTrue();
    assertThat(response.email()).isEqualTo("john@example.com");
    assertThat(response.reason()).isEqualTo("Email exists and is deliverable");
    mockServer.verify();
  }

  @Test
  void shouldReturnInvalidResponse_whenEmailIsInvalid() {
    mockServer
        .expect(requestTo(containsString("/api/validate/email")))
        .andRespond(
            withSuccess(
                """
                {
                  "email": "invalid@bad.email",
                  "valid": false,
                  "reason": "Domain does not accept email"
                }
                """,
                APPLICATION_JSON));

    EmailValidationResponse response = emailValidationClient.validateEmail("invalid@bad.email");

    assertThat(response).isNotNull();
    assertThat(response.valid()).isFalse();
    assertThat(response.email()).isEqualTo("invalid@bad.email");
    assertThat(response.reason()).contains("Domain");
    mockServer.verify();
  }

  @Test
  void shouldReturnInvalidResponse_whenServerReturns500() {
    mockServer
        .expect(requestTo(containsString("/api/validate/email")))
        .andRespond(withServerError());

    EmailValidationResponse response = emailValidationClient.validateEmail("any@example.com");

    assertThat(response).isNotNull();
    assertThat(response.valid()).isFalse();
    assertThat(response.reason()).contains("Validation service error");
    mockServer.verify();
  }

  @Test
  void shouldReturnInvalidResponse_whenServerReturns400() {
    mockServer
        .expect(requestTo(containsString("/api/validate/email")))
        .andRespond(withBadRequest());

    EmailValidationResponse response = emailValidationClient.validateEmail("not-an-email");

    assertThat(response).isNotNull();
    assertThat(response.valid()).isFalse();
    assertThat(response.reason()).contains("error");
    mockServer.verify();
  }

  @Test
  void shouldReturnInvalidResponse_whenServerReturns404() {
    mockServer
        .expect(requestTo(containsString("/api/validate/email")))
        .andRespond(withResourceNotFound());

    EmailValidationResponse response = emailValidationClient.validateEmail("user@example.com");

    assertThat(response).isNotNull();
    assertThat(response.valid()).isFalse();
    mockServer.verify();
  }

  @Test
  void shouldDeserializeResponse_withNullReason() {
    mockServer
        .expect(requestTo(containsString("/api/validate/email")))
        .andRespond(
            withSuccess(
                "{\"email\": \"minimal@test.com\", \"valid\": true, \"reason\": null}",
                APPLICATION_JSON));

    EmailValidationResponse response = emailValidationClient.validateEmail("minimal@test.com");

    assertThat(response).isNotNull();
    assertThat(response.valid()).isTrue();
    assertThat(response.email()).isEqualTo("minimal@test.com");
    assertThat(response.reason()).isNull();
    mockServer.verify();
  }
}
