package ru.mentee.power.crm.spring.exception;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.mentee.power.crm.spring.service.LeadEntityService;

/** Тест 500 Internal Server Error: сервис подменён на mock, выбрасывающий RuntimeException. */
@SpringBootTest
@ActiveProfiles("test")
@Import(GlobalExceptionHandler500Test.Config.class)
public class GlobalExceptionHandler500Test {

  @Autowired private WebApplicationContext context;

  private MockMvc mockMvc() {
    return MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  void shouldReturn500_whenUnexpectedExceptionOccurs() throws Exception {
    UUID id = UUID.randomUUID();

    String responseBody =
        mockMvc()
            .perform(get("/api/leads/{id}", id))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("Internal server error occurred"))
            .andExpect(jsonPath("$.status").value(500))
            .andExpect(jsonPath("$.error").value("Internal Server Error"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    assertFalse(responseBody.contains("at ru."), "Response must not contain stack trace");
    assertFalse(responseBody.contains("at java."), "Response must not contain stack trace");
  }

  @TestConfiguration
  static class Config {

    @Bean
    @Primary
    LeadEntityService leadEntityService() {
      LeadEntityService mock = mock(LeadEntityService.class);
      when(mock.getLeadById(any())).thenThrow(new RuntimeException("Unexpected"));
      return mock;
    }
  }
}
