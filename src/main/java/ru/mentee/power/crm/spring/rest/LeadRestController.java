package ru.mentee.power.crm.spring.rest;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.entity.LeadEntity;
import ru.mentee.power.crm.spring.dto.generated.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.generated.LeadResponse;
import ru.mentee.power.crm.spring.dto.generated.UpdateLeadRequest;
import ru.mentee.power.crm.spring.mapper.LeadMapper;
import ru.mentee.power.crm.spring.rest.generated.LeadApi;
import ru.mentee.power.crm.spring.service.LeadEntityService;

@RestController
@Validated
@RequiredArgsConstructor
public class LeadRestController implements LeadApi {

  private final LeadEntityService leadEntityService;
  private final LeadMapper leadMapper;

  @Override
  public ResponseEntity<List<LeadResponse>> getLeads() {
    List<LeadResponse> responses =
        leadEntityService.findAll().stream().map(leadMapper::toGeneratedResponse).toList();
    return ResponseEntity.ok(responses);
  }

  @Override
  public ResponseEntity<LeadResponse> getLeadById(UUID id) {
    LeadEntity lead = leadEntityService.getLeadById(id);
    return ResponseEntity.ok(leadMapper.toGeneratedResponse(lead));
  }

  @Override
  public ResponseEntity<LeadResponse> createLead(CreateLeadRequest createLeadRequest) {
    LeadEntity entity = leadMapper.toEntityFromApi(createLeadRequest);
    entity.setCreatedAt(Instant.now());
    LeadEntity saved = leadEntityService.createLead(entity);
    LeadResponse response = leadMapper.toGeneratedResponse(saved);
    URI location = URI.create("/api/leads/" + response.getId());
    return ResponseEntity.created(location).body(response);
  }

  @Override
  public ResponseEntity<LeadResponse> updateLead(UUID id, UpdateLeadRequest updateLeadRequest) {
    LeadEntity saved = leadEntityService.updateLead(id, updateLeadRequest);
    return ResponseEntity.ok(leadMapper.toGeneratedResponse(saved));
  }

  @Override
  public ResponseEntity<Void> deleteLead(UUID id) {
    leadEntityService.deleteLead(id);
    return ResponseEntity.noContent().build();
  }
}
