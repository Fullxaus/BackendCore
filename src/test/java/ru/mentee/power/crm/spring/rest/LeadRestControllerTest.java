package ru.mentee.power.crm.spring.rest;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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

/** Интеграционные тесты REST API: проверка HTTP статусов 200/201/204/404 и заголовка Location. */
@SpringBootTest
@ActiveProfiles("test")
public class LeadRestControllerTest {

  @Autowired private WebApplicationContext context;

  private MockMvc mockMvc() {
    return MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  void shouldReturn200_whenGetAllLeads() throws Exception {
    mockMvc()
        .perform(get("/api/leads"))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Type", containsString("application/json")));
  }

  @Test
  void shouldReturn404_whenGetNonExistentLead() throws Exception {
    UUID id = UUID.randomUUID();
    mockMvc().perform(get("/api/leads/{id}", id)).andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn201WithLocation_whenCreateLead() throws Exception {
    String json =
        """
        {"email":"rest-test-create@example.com","firstName":"John","lastName":"Doe","company":"Acme"}
        """;

    String location =
        mockMvc()
            .perform(post("/api/leads").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andReturn()
            .getResponse()
            .getHeader("Location");
    assert location != null && location.contains("/api/leads/");
  }

  @Test
  void shouldReturn404_whenDeleteNonExistentLead() throws Exception {
    UUID id = UUID.randomUUID();
    mockMvc().perform(delete("/api/leads/{id}", id)).andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn404_whenUpdateNonExistentLead() throws Exception {
    UUID id = UUID.randomUUID();
    String json =
        """
        {"email":"updated@example.com","firstName":"Jane","lastName":"Smith","company":"Updated Corp"}
        """;

    mockMvc()
        .perform(put("/api/leads/{id}", id).contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isNotFound());
  }
}
