package ru.mentee.power.crm.repository;

import ru.mentee.power.crm.model.Lead;

import java.util.*;

public class InMemoryLeadRepository implements Repository<Lead> {

    private final Map<UUID, Lead> storage = new HashMap<>();
    private final Map<String, UUID> emailIndex = new HashMap<>();

    @Override
    public void add(Lead entity) {

    }

    @Override
    public void remove(UUID id) {

    }

    @Override
    public Lead save(Lead lead) {
        storage.put(lead.id(), lead);
        emailIndex.put(lead.contact().email(), lead.id());
        return lead;
    }

    @Override
    public Optional<Lead> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
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

