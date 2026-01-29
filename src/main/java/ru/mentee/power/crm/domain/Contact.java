package ru.mentee.power.crm.domain;

public record Contact(
        String email,
        String phone,
        Address address
) {

    public Contact {
        if (email == null || email.isEmpty() || phone == null || phone.isEmpty() || address == null) {
            throw new IllegalArgumentException("Email, phone, and address cannot be null or empty");
        }
    }
    public Contact(String email) {
        this(email, "", null);
    }
}
