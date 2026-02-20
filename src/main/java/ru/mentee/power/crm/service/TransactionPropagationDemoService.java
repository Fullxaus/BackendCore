package ru.mentee.power.crm.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.repository.LeadDomainRepository;

import java.util.UUID;

/**
 * Демонстрация параметров propagation и isolation для @Transactional.
 * Используется в тестах для изучения поведения REQUIRED, REQUIRES_NEW, MANDATORY и уровней изоляции.
 */
@Service
public class TransactionPropagationDemoService {

    private final LeadDomainRepository repository;

    public TransactionPropagationDemoService(LeadDomainRepository repository) {
        this.repository = repository;
    }

    /** REQUIRED (по умолчанию): участвует в текущей транзакции или создаёт новую. */
    @Transactional(propagation = Propagation.REQUIRED)
    public Lead findWithRequired(UUID id) {
        return repository.findById(id);
    }

    /** REQUIRES_NEW: всегда создаёт новую транзакцию, приостанавливая текущую. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Lead findWithRequiresNew(UUID id) {
        return repository.findById(id);
    }

    /** MANDATORY: должен вызываться только внутри существующей транзакции, иначе исключение. */
    @Transactional(propagation = Propagation.MANDATORY)
    public Lead findWithMandatory(UUID id) {
        return repository.findById(id);
    }

    /** READ_COMMITTED: уровень изоляции — видит только закоммиченные данные. */
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Lead findWithReadCommitted(UUID id) {
        return repository.findById(id);
    }

    /** REPEATABLE_READ: повторные чтения в той же транзакции видят те же данные. */
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public Lead findWithRepeatableRead(UUID id) {
        return repository.findById(id);
    }

    /** Вызов метода с MANDATORY из метода с REQUIRED — одна транзакция. */
    @Transactional(propagation = Propagation.REQUIRED)
    public Lead requiredCallsMandatory(UUID id) {
        return findWithMandatory(id);
    }
}
