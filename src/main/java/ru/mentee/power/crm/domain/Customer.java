package ru.mentee.power.crm.domain;

import java.util.UUID;

public record Customer(
        UUID id,
        Contact contact,
        Address billingAddress,
        String loyaltyTier
) {
    public Customer {
        if (id == null || contact == null || billingAddress == null || loyaltyTier == null || loyaltyTier.isEmpty()) {
            throw new IllegalArgumentException("Id, contact, billingAddress, and loyaltyTier cannot be null or empty");
        }
        if (!loyaltyTier.equals("BRONZE") && !loyaltyTier.equals("SILVER") && !loyaltyTier.equals("GOLD")) {
            throw new IllegalArgumentException("Invalid loyaltyTier. Must be one of: BRONZE, SILVER, GOLD");
        }
    }
}
