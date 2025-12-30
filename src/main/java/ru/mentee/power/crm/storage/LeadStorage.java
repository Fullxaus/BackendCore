package ru.mentee.power.crm.storage;

import ru.mentee.power.crm.model.Lead;

import java.util.ArrayList;
import java.util.List;

public class LeadStorage {
    private List<Lead> leads = new ArrayList<>();

    /**
     * Добавляет лид в хранилище, если его email еще не существует.
     *
     * @param lead Лид для добавления.
     * @return true, если лид успешно добавлен, false, если email уже существует.
     */
    public boolean add(Lead lead) {
        if (leads.stream().anyMatch(l -> l.contact().email().equals(lead.contact().email()))) {
            return false; // Дубликат найден
        }
        leads.add(lead);
        return true; // Лид добавлен
    }

    /**
     * Возвращает список всех добавленных лидов.
     *
     * @return Список лидов.
     */
    public List<Lead> findAll() {
        return new ArrayList<>(leads); // defensive copy
    }

    /**
     * Возвращает количество добавленных лидов.
     *
     * @return Количество лидов.
     */
    public int size() {
        return leads.size();
    }
}
