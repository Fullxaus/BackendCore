package ru.mentee.power.crm.model;

import ru.mentee.power.crm.domain.Contact;

import java.util.Arrays;
import java.util.UUID;


public record Lead(
        UUID id,
        Contact contact,
        String company,
        String status
) {
    public Lead {
        if (id == null || contact == null || company == null || company.isEmpty() || status == null || status.isEmpty()) {
            throw new IllegalArgumentException("Id, contact, company, and status cannot be null or empty");
        }
        boolean validStatus = Arrays.stream(LeadStatus.values())
                .anyMatch(s -> s.name().equals(status))
                || "CONVERTED".equals(status); // backward compatibility
        if (!validStatus) {
            throw new IllegalArgumentException("Invalid status. Must be one of: "
                    + Arrays.toString(LeadStatus.values()));
        }
    }
}