package ru.mentee.power.crm.storage;

import ru.mentee.power.crm.domain.Lead;

public class LeadStorage {
    private Lead[] leads = new Lead[100];

    /**
     * Добавляет лид в хранилище, если его email еще не существует.
     *
     * @param lead Лид для добавления.
     * @return true, если лид успешно добавлен, false, если email уже существует.
     */
    public boolean add(Lead lead) {
        // Проверка на дубликат
        for (Lead existingLead : leads) {
            if (existingLead != null && existingLead.email().equals(lead.email())) {
                return false; // Дубликат найден
            }
        }

        // Поиск первой свободной ячейки
        for (int i = 0; i < leads.length; i++) {
            if (leads[i] == null) {
                leads[i] = lead;
                return true; // Лид добавлен
            }
        }

        // Если хранилище заполнено
        throw new IllegalStateException("Storage is full, cannot add more leads");
    }

    /**
     * Возвращает массив всех добавленных лидов.
     *
     * @return Массив лидов.
     */
    public Lead[] findAll() {
        // Подсчет ненулевых элементов
        int count = 0;
        for (Lead lead : leads) {
            if (lead != null) {
                count++;
            }
        }

        // Создание массива для результата
        Lead[] result = new Lead[count];

        // Заполнение результата ненулевыми элементами
        int resultIndex = 0;
        for (Lead lead : leads) {
            if (lead != null) {
                result[resultIndex++] = lead;
            }
        }

        return result;
    }

    /**
     * Возвращает количество добавленных лидов.
     *
     * @return Количество лидов.
     */
    public int size() {
        int count = 0;
        for (Lead lead : leads) {
            if (lead != null) {
                count++;
            }
        }
        return count;
    }
}
