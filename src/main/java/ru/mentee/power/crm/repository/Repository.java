package ru.mentee.power.crm.repository;

import ru.mentee.power.crm.model.Lead;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface Repository<T> {
    void add(T entity);
    void remove(UUID id);

    T save(T entity);

    Optional<T> findById(UUID id);
    List<T> findAll();

    void delete(UUID id);

    Optional<T> findByEmail(String email);
}
