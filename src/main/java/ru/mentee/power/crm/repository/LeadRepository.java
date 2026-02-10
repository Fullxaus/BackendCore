package ru.mentee.power.crm.repository;

import ru.mentee.power.crm.model.Lead;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeadRepository {

    void save(Lead lead);

    Lead findById(UUID id);

    List<Lead> findAll();

    void delete(UUID id);

    Optional<Lead> findByEmail(String email);

    default int size() {
        return findAll().size();
    }
}
