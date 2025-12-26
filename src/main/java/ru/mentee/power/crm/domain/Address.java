package ru.mentee.power.crm.domain;

public record Address(
        String city,
        String street,
        String zip
) {
    // Компактный конструктор с валидацией
    public Address {
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("Город не может быть пустым");
        }
        if (zip == null || zip.isBlank()) {
            throw new IllegalArgumentException("Почтовый индекс не может быть пустым");
        }
    }
}
