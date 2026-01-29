package ru.mentee.power.crm.util;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

/**
 * Утилита для генерации тестовых данных лидов.
 * Создаёт набор лидов с различными компаниями и статусами.
 */
public class TestDataUtils {

    private static final List<String> EMAIL_DOMAINS = List.of("@gmail.com", "@yandex.ru", "@mail.ru");
    private static final List<String> COMPANIES = List.of(
            "ABC Company", "XYZ Corporation", "Acme Technologies", "Pro Services", "InnoCorp"
    );
    private static final List<String> PHONES = List.of("+79001234567", "+79112345678", "+79223456789");
    private static final List<String> ADDRESS_CITIES = List.of("Москва", "Санкт-Петербург", "Новосибирск", "Екатеринбург", "Казань");
    private static final List<String> ADDRESS_STREETS = List.of("улица Ленина", "Проспект Мира", "Тверская улица", "Невский проспект");
    private static final List<String> ZIP_CODES = List.of("123456", "654321", "987654", "456789");

    private static final Random random = new Random();


    /**
     * Создает и добавляет набор тестовых лидов.
     *
     * @param leadService Сервис для работы с лидами.
     */
    public static void initializeTestLeads(LeadService leadService) {
        final int NUMBER_OF_LEADS = 5; // Можно настроить любое нужное количество лидов

        for (int i = 0; i < NUMBER_OF_LEADS; i++) {
            String emailDomain = EMAIL_DOMAINS.get(random.nextInt(EMAIL_DOMAINS.size()));
            String uniquePart = Long.toHexString(System.currentTimeMillis() + i);
            String email = "user-" + uniquePart + emailDomain;

            String company = COMPANIES.get(random.nextInt(COMPANIES.size()));
            String phone = PHONES.get(random.nextInt(PHONES.size()));

            String city = ADDRESS_CITIES.get(random.nextInt(ADDRESS_CITIES.size()));
            String street = ADDRESS_STREETS.get(random.nextInt(ADDRESS_STREETS.size()));
            String zip = ZIP_CODES.get(random.nextInt(ZIP_CODES.size()));

            Address address = new Address(city, street, zip);

            Lead lead = new Lead(
                    UUID.randomUUID(),
                    new Contact(email, phone, address),
                    company,
                    LeadStatus.NEW.name()
            );

            leadService.addLead(email, company, LeadStatus.NEW, address, phone);
        }
    }
}