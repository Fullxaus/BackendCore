package ru.mentee.power.crm.domain;

import java.util.UUID;

public class Lead {
    private UUID id;
    private String email;
    private String phone;
    private String company;
    private String status;

    /**
     * Конструктор для создания нового лида с генерацией случайного UUID.
     *
     * @param email   Электронный адрес лида.
     * @param phone   Номер телефона лида.
     * @param company Компания лида.
     * @param status  Статус лида.
     */
    public Lead(String email, String phone, String company, String status) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.status = status;
    }

    /**
     * Конструктор для создания лида с существующим UUID.
     *
     * @param id      Уникальный идентификатор лида.
     * @param email   Электронный адрес лида.
     * @param phone   Номер телефона лида.
     * @param company Компания лида.
     * @param status  Статус лида.
     */
    public Lead(UUID id, String email, String phone, String company, String status) {
        this.id = id;
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCompany() {
        return company;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Lead{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", company='" + company + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
