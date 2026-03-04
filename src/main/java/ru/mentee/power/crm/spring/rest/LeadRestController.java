package ru.mentee.power.crm.spring.rest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

/**
 * REST-контроллер для работы с лидами (JSON API).
 *
 * <p>В отличие от {@link ru.mentee.power.crm.spring.controller.LeadController} (JTE/HTML): здесь
 * методы возвращают {@link Lead} / {@link java.util.List}{@code <Lead>} — Spring сериализует их в
 * JSON (Content-Type: application/json). LeadController возвращает {@link String} (имя view).
 */
@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadRestController {

  private final LeadService leadService;

  @GetMapping
  public List<Lead> getAllLeads() {
    return leadService.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Lead> getLeadById(@PathVariable UUID id) {
    Optional<Lead> lead = leadService.findById(id);
    return lead.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<Lead> createLead(@RequestBody CreateLeadRequest request) {
    LeadStatus status =
        request.status() != null ? LeadStatus.valueOf(request.status()) : LeadStatus.NEW;
    Address address =
        new Address(
            request.city() != null ? request.city() : "-",
            request.street() != null ? request.street() : "-",
            request.zip() != null ? request.zip() : "-");
    String company = resolveCompany(request);
    Lead lead =
        leadService.addLead(
            request.email(),
            company,
            status,
            address,
            request.phone() != null ? request.phone() : "-");
    return ResponseEntity.ok(lead);
  }

  private static String resolveCompany(CreateLeadRequest request) {
    if (request.company() != null && !request.company().isBlank()) {
      return request.company();
    }
    String first = request.firstName() != null ? request.firstName() : "";
    String last = request.lastName() != null ? request.lastName() : "";
    String combined = (first + " " + last).trim();
    return combined.isEmpty() ? "-" : combined;
  }

}
