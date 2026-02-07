package ru.mentee.power.crm.repository;

import org.springframework.stereotype.Component;
import ru.mentee.power.crm.model.Lead;

import java.util.*;

@Component
public class LeadRepository   {
    private final Map<UUID, Lead> storage = new HashMap<>();

    public void save(Lead lead) {
        storage.put(lead.id(), lead);
    }

    public Lead findById(UUID id) {
        return storage.get(id);
    }

    public List<Lead> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void delete(UUID id) {
        storage.remove(id);
    }

    public int size() {
        return storage.size();
    }

    public Optional<Lead> findByEmail(String email) {
        return storage.values().stream()
                .filter(lead -> lead.contact().email().equals(email))
                .findFirst();
    }
}
