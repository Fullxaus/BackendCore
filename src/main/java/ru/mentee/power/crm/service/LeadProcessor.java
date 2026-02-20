package ru.mentee.power.crm.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadDomainRepository;

import java.util.UUID;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

/**
 * Отдельный сервис для обработки одного лида в своей транзакции (REQUIRES_NEW).
 * Решает self-invocation: вызов через leadProcessor.processSingleLead(id) из LeadService
 * идёт через proxy, поэтому создаётся отдельная транзакция.
 */
@Service
public class LeadProcessor {

    private final LeadDomainRepository repository;

    public LeadProcessor(LeadDomainRepository repository) {
        this.repository = repository;
    }

    /**
     * Обрабатывает один лид в отдельной транзакции (REQUIRES_NEW).
     * При ошибке откатывается только эта операция, остальные зафиксированные остаются.
     */
    @Transactional(propagation = REQUIRES_NEW)
    public void processSingleLead(UUID id) {
        Lead lead = repository.findById(id);
        if (lead != null) {
            Lead updated = new Lead(lead.id(), lead.contact(), lead.company(), LeadStatus.PROCESSED.name());
            repository.save(updated);
        }
    }
}
