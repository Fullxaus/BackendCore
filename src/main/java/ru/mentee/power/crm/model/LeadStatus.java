package ru.mentee.power.crm.model;


public enum LeadStatus {
    NEW("Новый"),
    CONTACTED("На связи"),
    QUALIFIED("Квалифицирован"),
    CONVERTED("Конвертирован в сделку"),
    PROCESSED("Обработан"),
    ORDER_FORMING("Заказ формируется"),
    SORTED("Отсортирован"),
    ASSEMBLED("Собран"),
    ON_THE_WAY_TO_PICKUP_POINT("В пути на пункт выдачи"),
    DELIVERED("Доставлен"),
    CANCELED("Отменен");

    private final String description;

    LeadStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}