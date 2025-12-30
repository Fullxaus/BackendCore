package ru.mentee.power.crm.domain;

public record Address(
        String city,
        String street,
        String zip
) {
    public Address {
        if (city == null || city.isEmpty() || zip == null || zip.isEmpty()) {
            throw new IllegalArgumentException("City and zip cannot be null or empty");
        }
    }
}
