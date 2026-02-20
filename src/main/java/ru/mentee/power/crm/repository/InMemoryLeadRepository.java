package ru.mentee.power.crm.repository;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.model.Lead;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("default")
public class InMemoryLeadRepository implements LeadDomainRepository {

    private final Map<UUID, Lead> storage = new HashMap<>();
    private final Map<String, UUID> emailIndex = new HashMap<>();

    @Override
    public void save(Lead lead) {
        storage.put(lead.id(), lead);
        emailIndex.put(lead.contact().email(), lead.id());
    }

    @Override
    public Lead findById(UUID id) {
        return storage.get(id);
    }

    @Override
    public List<Lead> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(UUID id) {
        Lead lead = storage.remove(id);
        if (lead != null) {
            emailIndex.remove(lead.contact().email());
        }
    }

    @Override
    public Optional<Lead> findByEmail(String email) {
        UUID id = emailIndex.get(email);
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(storage.get(id));
    }
}
