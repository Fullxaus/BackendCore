package ru.mentee.power.crm.repository;

import org.springframework.stereotype.Component;
import ru.mentee.power.crm.model.LeadStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * In-memory хранилище записей статусов лидов.
 * Ключ — идентификатор записи, значение — статус.
 */
@Component
public class MemoryRepositoryLeadStatus {

    private final Map<UUID, LeadStatus> storage = new HashMap<>();

    /**
     * Сохраняет статус и возвращает его идентификатор.
     */
    public UUID save(LeadStatus leadStatus) {
        if (leadStatus == null) {
            throw new IllegalArgumentException("leadStatus must not be null");
        }
        UUID id = UUID.randomUUID();
        storage.put(id, leadStatus);
        return id;
    }

    /**
     * Возвращает все сохранённые статусы (неизменяемая копия).
     */
    public List<LeadStatus> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(storage.values()));
    }

    /**
     * Ищет запись по идентификатору.
     */
    public Optional<LeadStatus> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * Проверяет, пусто ли хранилище (для инициализации при старте).
     */
    public boolean isEmpty() {
        return storage.isEmpty();
    }
}
