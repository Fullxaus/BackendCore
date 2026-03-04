package ru.mentee.power.crm.spring.client;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "email.validation.base-url=http://localhost:8089")
@Disabled("WireMock Jetty server is not available on current Java runtime; kept for reference")
class EmailValidationClientWireMockTest {

  private static WireMockServer wireMockServer;

  @Autowired private EmailValidationClient emailValidationClient;

  @BeforeAll
  static void startServer() {
    wireMockServer =
        new WireMockServer(WireMockConfiguration.options().port(8089).bindAddress("localhost"));
    wireMockServer.start();
    configureFor("localhost", 8089);
  }

  @AfterAll
  static void stopServer() {
    if (wireMockServer != null) {
      wireMockServer.stop();
    }
  }

  @Test
  void shouldReturnValid_whenEmailIsCorrect() {
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .withQueryParam("email", equalTo("john@example.com"))
            .willReturn(
                okJson(
                    """
                    {
                      "email": "john@example.com",
                      "valid": true,
                      "reason": "Email exists"
                    }
                    """)));

    EmailValidationResponse response = emailValidationClient.validateEmail("john@example.com");

    assertThat(response).isNotNull();
    assertThat(response.valid()).isTrue();
    assertThat(response.email()).isEqualTo("john@example.com");
  }

  @Test
  void shouldReturnInvalid_whenEmailIsIncorrect() {
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .withQueryParam("email", equalTo("invalid-email"))
            .willReturn(
                okJson(
                    """
                    {
                      "email": "invalid-email",
                      "valid": false,
                      "reason": "Invalid email format"
                    }
                    """)));

    EmailValidationResponse response = emailValidationClient.validateEmail("invalid-email");

    assertThat(response).isNotNull();
    assertThat(response.valid()).isFalse();
    assertThat(response.email()).isEqualTo("invalid-email");
  }

  @Test
  void shouldHandleServerError_whenExternalServiceFails() {
    stubFor(
        get(urlPathEqualTo("/api/validate/email"))
            .willReturn(serverError().withBody("Internal Server Error")));

    EmailValidationResponse response = emailValidationClient.validateEmail("any@example.com");

    assertThat(response).isNotNull();
    assertThat(response.valid()).isFalse();
    assertThat(response.reason()).contains("Validation service error");
  }
}
