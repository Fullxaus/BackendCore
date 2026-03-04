package ru.mentee.power.crm.spring.repository;

import java.util.Optional;
import java.util.UUID;
import ru.mentee.power.crm.model.Lead;

/**
 * Repository interface for DealService to look up Leads by ID. Adapter delegates to the main
 * repository.
 */
public interface LeadRepository {
  Optional<Lead> findById(UUID id);
}
