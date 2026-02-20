package ru.mentee.power.crm.service;

import org.springframework.stereotype.Service;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.MemoryRepositoryLeadStatus;

import java.util.List;
import java.util.UUID;

/**
 * Сервис для работы со статусами лидов.
 * Добавление и получение статусов выполняется через этот сервис.
 */
@Service
public class LeadStatusService {

    private final MemoryRepositoryLeadStatus repository;

    public LeadStatusService(MemoryRepositoryLeadStatus repository) {
        this.repository = repository;
    }

    /**
     * Добавляет статус в хранилище и возвращает его идентификатор.
     */
    public UUID addStatus(LeadStatus leadStatus) {
        return repository.save(leadStatus);
    }

    /**
     * Возвращает список всех сохранённых статусов.
     */
    public List<LeadStatus> findAllStatuses() {
        return repository.findAll();
    }

    /**
     * Инициализирует хранилище всеми значениями enum LeadStatus, если оно пусто.
     * Вызывать при старте приложения (например, из DataInitializer или Main).
     */
    public void ensureStatusesInitialized() {
        if (!repository.isEmpty()) {
            return;
        }
        for (LeadStatus status : LeadStatus.values()) {
            repository.save(status);
        }
    }
}
