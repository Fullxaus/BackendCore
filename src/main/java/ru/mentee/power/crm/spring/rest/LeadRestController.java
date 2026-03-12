package ru.mentee.power.crm.spring.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.entity.LeadEntity;
import ru.mentee.power.crm.spring.dto.CreateLeadRequest;
import ru.mentee.power.crm.spring.dto.LeadResponse;
import ru.mentee.power.crm.spring.dto.UpdateLeadRequest;
import ru.mentee.power.crm.spring.mapper.LeadMapper;
import ru.mentee.power.crm.spring.service.LeadEntityService;

@RestController
@RequestMapping("/api/leads")
@Validated
@RequiredArgsConstructor
public class LeadRestController {

  private final LeadEntityService leadEntityService;
  private final LeadMapper leadMapper;

  @GetMapping
  public ResponseEntity<List<LeadResponse>> getAllLeads() {
    List<LeadResponse> responses =
        leadEntityService.findAll().stream().map(leadMapper::toResponse).toList();
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/{id}")
  public ResponseEntity<LeadResponse> getLeadById(
      @PathVariable @NotNull(message = "ID лида обязателен") UUID id) {
    return leadEntityService
        .findById(id)
        .map(leadMapper::toResponse)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<LeadResponse> createLead(@Valid @RequestBody CreateLeadRequest request) {
    LeadEntity entity = leadMapper.toEntity(request);
    entity.setCreatedAt(Instant.now());
    LeadEntity saved = leadEntityService.save(entity);
    LeadResponse response = leadMapper.toResponse(saved);
    URI location = URI.create("/api/leads/" + response.id());
    return ResponseEntity.created(location).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<LeadResponse> updateLead(
      @PathVariable @NotNull(message = "ID лида обязателен") UUID id,
      @Valid @RequestBody UpdateLeadRequest request) {
    return leadEntityService
        .findById(id)
        .map(
            entity -> {
              leadMapper.updateEntity(request, entity);
              LeadEntity saved = leadEntityService.save(entity);
              LeadResponse response = leadMapper.toResponse(saved);
              return ResponseEntity.ok(response);
            })
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteLead(
      @PathVariable @NotNull(message = "ID лида обязателен") UUID id) {
    if (!leadEntityService.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    leadEntityService.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
