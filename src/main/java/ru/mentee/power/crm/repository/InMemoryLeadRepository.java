package ru.mentee.power.crm.repository;

import ru.mentee.power.crm.domain.Lead;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryLeadRepository implements Repository<Lead> {
    private final List<Lead> leads;

    public InMemoryLeadRepository() {
        this.leads = new ArrayList<>();
    }

    @Override
    public void add(Lead lead) {
        if (leads.stream().noneMatch(l -> l.id().equals(lead.id()))) {
            leads.add(lead);
        }
    }

    @Override
    public void remove(UUID id) {
        leads.removeIf(lead -> lead.id().equals(id));
    }

    @Override
    public Optional<Lead> findById(UUID id) {
        return leads.stream()
                .filter(lead -> lead.id().equals(id))
                .findFirst();
    }

    @Override
    public List<Lead> findAll() {
        return new ArrayList<>(leads); // defensive copy
    }
}
