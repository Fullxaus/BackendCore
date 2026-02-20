package ru.mentee.power.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadDomainRepository;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Сервис для демонстрации pessimistic и optimistic locking.
 * Конверсия Lead→Deal использует findByIdForUpdate (PESSIMISTIC_WRITE).
 * Обычные обновления идут через обычный findById/save и при конфликте версий
 * выбрасывается OptimisticLockException — перехватывается и логируется.
 */
@Service
public class LeadLockingService {

    private static final Logger log = LoggerFactory.getLogger(LeadLockingService.class);

    private final LeadDomainRepository repository;
    private final LeadService leadService;

    public LeadLockingService(LeadDomainRepository repository, LeadService leadService) {
        this.repository = repository;
        this.leadService = leadService;
    }

    /**
     * Конверсия лида в сделку с пессимистической блокировкой.
     * Использует findByIdForUpdate — вторая транзакция ждёт завершения первой.
     */
    @Transactional
    public Deal convertWithPessimisticLock(UUID leadId, BigDecimal amount) {
        return leadService.convertLeadToDeal(leadId, amount);
    }

    /**
     * Обновление статуса лида без явной блокировки (optimistic locking через @Version).
     * При конфликте версий выбрасывается OptimisticLockException — перехватываем и логируем.
     *
     * @return обновлённый Lead или null при конфликте версий (можно повторить операцию)
     */
    @Transactional
    public Lead updateStatusWithOptimisticLock(UUID leadId, LeadStatus newStatus) {
        Lead lead = repository.findById(leadId);
        if (lead == null) {
            throw new IllegalArgumentException("Lead not found: " + leadId);
        }
        try {
            Lead updated = new Lead(
                    lead.id(),
                    lead.contact(),
                    lead.company(),
                    newStatus.name()
            );
            repository.save(updated);
            return updated;
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            log.warn("Optimistic lock conflict updating lead {} to status {}: {}. Retry or return error to client.",
                    leadId, newStatus, e.getMessage());
            throw e;
        }
    }

    /**
     * Обновление статуса с пессимистической блокировкой (findByIdForUpdate + save).
     * Две транзакции выполняются последовательно — оба обновления успешны.
     */
    @Transactional
    public Lead updateStatusWithPessimisticLock(UUID leadId, LeadStatus newStatus) {
        Lead lead = repository.findByIdForUpdate(leadId);
        if (lead == null) {
            throw new IllegalArgumentException("Lead not found: " + leadId);
        }
        Lead updated = new Lead(lead.id(), lead.contact(), lead.company(), newStatus.name());
        repository.save(updated);
        return updated;
    }

    /**
     * Загрузка лида с пессимистической блокировкой (для демонстрации и тестов).
     */
    @Transactional
    public Lead findByIdForUpdate(UUID leadId) {
        return repository.findByIdForUpdate(leadId);
    }
}
