package ru.mentee.power.crm.repository;

import ru.mentee.power.crm.model.Lead;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Доменный интерфейс репозитория для работы с Lead (domain model).
 * Используется для абстракции над различными реализациями (InMemory, JPA).
 */
public interface LeadDomainRepository {

    void save(Lead lead);

    Lead findById(UUID id);

    List<Lead> findAll();

    void delete(UUID id);

    Optional<Lead> findByEmail(String email);

    default int size() {
        return findAll().size();
    }
}
