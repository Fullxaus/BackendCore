package ru.mentee.power.crm.storage;

import ru.mentee.power.crm.domain.Lead;

public class LeadStorage {
    private Lead[] leads = new Lead[100];
    private int size = 0;

    /**
     * Добавляет лид в хранилище, если его email еще не существует.
     *
     * @param lead Лид для добавления.
     * @return true, если лид успешно добавлен, false, если email уже существует.
     */
    public boolean add(Lead lead) {
        // Проверка на дубликат
        for (int i = 0; i < size; i++) {
            if (leads[i].contact().email().equals(lead.contact().email())) {
                return false; // Дубликат найден
            }
        }

        // Если хранилище заполнено
        if (size == leads.length) {
            throw new IllegalStateException("Storage is full, cannot add more leads");
        }

        leads[size++] = lead;
        return true; // Лид добавлен
    }

    /**
     * Возвращает массив всех добавленных лидов.
     *
     * @return Массив лидов.
     */
    public Lead[] findAll() {
        Lead[] result = new Lead[size];
        System.arraycopy(leads, 0, result, 0, size);
        return result;
    }

    /**
     * Возвращает количество добавленных лидов.
     *
     * @return Количество лидов.
     */
    public int size() {
        return size;
    }
}

