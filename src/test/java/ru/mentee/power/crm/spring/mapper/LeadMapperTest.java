package ru.mentee.power.crm.spring.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mentee.power.crm.entity.LeadEntity;
import ru.mentee.power.crm.spring.dto.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.LeadResponse;
import ru.mentee.power.crm.spring.dto.UpdateLeadRequest;

@SpringBootTest
class LeadMapperTest {

  @Autowired private LeadMapper leadMapper;

  @Test
  void shouldMapCreateRequestToEntity_whenValidData() {
    CreateLeadRequest request = new CreateLeadRequest("test@example.com", "John", "Doe", "Acme");

    LeadEntity entity = leadMapper.toEntity(request);

    assertThat(entity.getId()).isNull();
    assertThat(entity.getEmail()).isEqualTo("test@example.com");
    assertThat(entity.getCompanyName()).isEqualTo("Acme");
    assertThat(entity.getStatus()).isEqualTo("NEW");
  }

  @Test
  void shouldMapEntityToResponse_whenValidEntity() {
    UUID id = UUID.randomUUID();
    LeadEntity entity = new LeadEntity();
    entity.setId(id);
    entity.setEmail("entity@example.com");
    entity.setCompanyName("Company");
    Instant created = Instant.now().truncatedTo(java.time.temporal.ChronoUnit.MILLIS);
    entity.setCreatedAt(created);

    LeadResponse response = leadMapper.toResponse(entity);

    assertThat(response.id()).isEqualTo(id);
    assertThat(response.email()).isEqualTo("entity@example.com");
    assertThat(response.company()).isEqualTo("Company");
    assertThat(response.createdAt()).isEqualTo(LocalDateTime.ofInstant(created, ZoneOffset.UTC));
  }

  @Test
  void shouldUpdateEntityInPlace_whenUpdateRequest() {
    LeadEntity entity = new LeadEntity();
    entity.setId(UUID.randomUUID());
    entity.setEmail("old@example.com");
    entity.setCompanyName("Old");
    entity.setStatus("NEW");

    UpdateLeadRequest request = new UpdateLeadRequest("new@example.com", "Jane", "Doe", "NewCo");

    leadMapper.updateEntity(request, entity);

    assertThat(entity.getId()).isNotNull();
    assertThat(entity.getEmail()).isEqualTo("new@example.com");
    assertThat(entity.getCompanyName()).isEqualTo("NewCo");
    assertThat(entity.getStatus()).isEqualTo("NEW");
  }
}
