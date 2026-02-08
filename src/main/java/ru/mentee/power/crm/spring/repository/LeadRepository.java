package ru.mentee.power.crm.spring.repository;

import ru.mentee.power.crm.model.Lead;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for DealService to look up Leads by ID.
 * Adapter delegates to the main repository.
 */
public interface LeadRepository {
    Optional<Lead> findById(UUID id);
}
