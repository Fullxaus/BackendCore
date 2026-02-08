package ru.mentee.power.crm.spring.repository;

import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.model.Lead;

import java.util.Optional;
import java.util.UUID;

@Repository
public class LeadRepositoryAdapter implements LeadRepository {
    private final ru.mentee.power.crm.repository.LeadRepository delegate;

    public LeadRepositoryAdapter(ru.mentee.power.crm.repository.LeadRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public Optional<Lead> findById(UUID id) {
        Lead lead = delegate.findById(id);
        return Optional.ofNullable(lead);
    }
}
