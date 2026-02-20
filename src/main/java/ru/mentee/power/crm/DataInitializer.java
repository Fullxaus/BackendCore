package ru.mentee.power.crm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.service.LeadStatusService;
import ru.mentee.power.crm.spring.repository.DealRepository;
import ru.mentee.power.crm.spring.service.DealService;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    public static final UUID QUALIFIED_LEAD_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    private final LeadService leadService;
    private final ru.mentee.power.crm.repository.LeadDomainRepository coreLeadRepository;
    private final DealService dealService;
    private final DealRepository dealRepository;
    private final LeadStatusService leadStatusService;

    public DataInitializer(LeadService leadService,
                           ru.mentee.power.crm.repository.LeadDomainRepository coreLeadRepository,
                           DealService dealService,
                           DealRepository dealRepository,
                           LeadStatusService leadStatusService) {
        this.leadService = leadService;
        this.coreLeadRepository = coreLeadRepository;
        this.dealService = dealService;
        this.dealRepository = dealRepository;
        this.leadStatusService = leadStatusService;
    }

    @Override
    public void run(String... args) {
        log.info("Начало инициализации тестовых данных...");

        // Инициализация статусов лидов через LeadStatusService
        leadStatusService.ensureStatusesInitialized();
        log.info("Статусы лидов инициализированы: {} записей", leadStatusService.findAllStatuses().size());

        // Безопасное добавление лидов - игнорируем ошибки дублирования при повторном запуске
        addLeadIfNotExists("test1@example.com", "Company1", LeadStatus.NEW, new Address("Moscow", "Suvorova", "123456"), "+71234567890");
        addLeadIfNotExists("test2@example.com", "Company2", LeadStatus.NEW, new Address("St.Petersburg", "Pushkinskaya", "987654"), "+79876543210");
        addLeadIfNotExists("test3@example.com", "Company3", LeadStatus.NEW, new Address("Kazan", "Kazanskaya", "111111"), "+71111111111");
        addLeadIfNotExists("test4@example.com", "Company4", LeadStatus.NEW, new Address("Novosibirsk", "Novosibirskaya", "222222"), "+72222222222");
        addLeadIfNotExists("test5@example.com", "Company5", LeadStatus.NEW, new Address("Ekaterinburg", "Lermontova", "333333"), "+73333333333");
        
        // Заполняем все обязательные поля для существующих лидов
        fillMissingFieldsForAllLeads();
        
        int totalLeads = leadService.findAll().size();
        log.info("Инициализация завершена. Всего лидов в базе: {}", totalLeads);

        // Lead для Scenario 1: convertLeadToDeal
        // Проверяем, существует ли уже лид с таким ID
        if (coreLeadRepository.findById(QUALIFIED_LEAD_ID) == null) {
            Lead qualifiedLead = new Lead(QUALIFIED_LEAD_ID,
                    new Contact("ivan@example.com", "+79991234567", new Address("Moscow", "Lenina", "101000")),
                    "Иван Петров",
                    LeadStatus.QUALIFIED.name());
            coreLeadRepository.save(qualifiedLead);
        }

        // 10 Deals для Kanban (Scenario 4): 3 NEW, 2 QUALIFIED, 2 PROPOSAL_SENT, 1 NEGOTIATION, 1 WON, 1 LOST
        var leadIds = leadService.findAll().stream().limit(6).map(Lead::id).toList();
        if (leadIds.size() >= 6) {
            createDealsForKanban(leadIds);
        }
    }
    
    /**
     * Заполняет все обязательные поля для существующих лидов, если они пустые или null.
     * Это исправляет проблему с данными, которые были созданы до добавления валидации.
     */
    private void fillMissingFieldsForAllLeads() {
        log.info("Проверка и заполнение обязательных полей для всех лидов...");
        var allLeads = leadService.findAll();
        int updatedCount = 0;
        int checkedCount = 0;
        
        for (Lead lead : allLeads) {
            checkedCount++;
            boolean needsUpdate = false;
            String email = lead.contact().email();
            String phone = lead.contact().phone();
            String company = lead.company();
            Address address = lead.contact().address();
            
            log.debug("Проверка лида {}: email={}, phone={}, company={}, city={}, zip={}", 
                    lead.id(), email, phone, company, 
                    address != null ? address.city() : "null",
                    address != null ? address.zip() : "null");
            
            // Проверяем и заполняем email - более строгая проверка
            if (email == null || email.isEmpty() || 
                email.equals("unknown@example.com") || 
                email.trim().isEmpty()) {
                // Генерируем уникальный email на основе полного UUID (без дефисов)
                String uniqueEmail = "lead" + lead.id().toString().replace("-", "") + "@example.com";
                
                // Проверяем, не существует ли уже лид с таким email
                Optional<Lead> existingLead = leadService.findByEmail(uniqueEmail);
                int counter = 1;
                while (existingLead.isPresent() && !existingLead.get().id().equals(lead.id())) {
                    // Если email уже занят другим лидом, добавляем суффикс
                    uniqueEmail = "lead" + lead.id().toString().replace("-", "") + "_" + counter + "@example.com";
                    existingLead = leadService.findByEmail(uniqueEmail);
                    counter++;
                }
                
                email = uniqueEmail;
                needsUpdate = true;
                log.debug("Обновление email для лида {}: {}", lead.id(), email);
            }
            
            // Проверяем и заполняем phone - более строгая проверка
            if (phone == null || phone.isEmpty() || 
                phone.equals("-") || 
                phone.trim().isEmpty() ||
                phone.length() < 5) {
                phone = "+79991234567";
                needsUpdate = true;
                log.debug("Обновление phone для лида {}: {}", lead.id(), phone);
            }
            
            // Проверяем и заполняем company - более строгая проверка
            if (company == null || company.isEmpty() || 
                company.equals("Unknown") || 
                company.trim().isEmpty()) {
                company = "Company " + lead.id().toString().substring(0, 8);
                needsUpdate = true;
                log.debug("Обновление company для лида {}: {}", lead.id(), company);
            }
            
            // Проверяем и заполняем address - более строгая проверка
            boolean addressNeedsUpdate = false;
            String city = address != null ? address.city() : null;
            String street = address != null ? address.street() : null;
            String zip = address != null ? address.zip() : null;
            
            if (address == null || 
                city == null || city.isEmpty() || city.equals("-") || city.trim().isEmpty() ||
                zip == null || zip.isEmpty() || zip.equals("-") || zip.trim().isEmpty()) {
                addressNeedsUpdate = true;
            }
            
            if (addressNeedsUpdate) {
                city = (city != null && !city.isEmpty() && !city.equals("-")) ? city : "Moscow";
                street = (street != null && !street.isEmpty() && !street.equals("-")) ? street : "Unknown Street";
                zip = (zip != null && !zip.isEmpty() && !zip.equals("-")) ? zip : "000000";
                address = new Address(city, street, zip);
                needsUpdate = true;
                log.debug("Обновление address для лида {}: city={}, street={}, zip={}", 
                        lead.id(), city, street, zip);
            }
            
            // Обновляем лид, если нужно
            if (needsUpdate) {
                try {
                    LeadStatus status = parseLeadStatus(lead.status());
                    // Создаем новый Contact с обновленными данными
                    Contact updatedContact = new Contact(email, phone, address);
                    // Создаем обновленный Lead
                    Lead updatedLead = new Lead(lead.id(), updatedContact, company, status.name());
                    // Сохраняем через repository напрямую, чтобы обновить все поля включая адрес
                    coreLeadRepository.save(updatedLead);
                    updatedCount++;
                    log.info("Обновлен лид {}: email={}, phone={}, company={}, city={}, zip={}", 
                            lead.id(), email, phone, company, address.city(), address.zip());
                } catch (Exception e) {
                    log.error("Ошибка при обновлении лида {}: {}", lead.id(), e.getMessage(), e);
                    log.error("Текущие значения: email={}, phone={}, company={}, address={}", 
                            email, phone, company, address);
                }
            }
        }
        
        log.info("Заполнение обязательных полей завершено. Проверено лидов: {}, обновлено: {}", 
                checkedCount, updatedCount);
    }
    
    private LeadStatus parseLeadStatus(String value) {
        if (value == null || value.isBlank()) {
            return LeadStatus.NEW;
        }
        try {
            return LeadStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            return LeadStatus.NEW;
        }
    }

    private void addLeadIfNotExists(String email, String company, LeadStatus status, Address address, String phone) {
        try {
            leadService.addLead(email, company, status, address, phone);
            log.debug("Добавлен новый лид: {}", email);
        } catch (IllegalStateException e) {
            // Лид уже существует - игнорируем ошибку при повторном запуске
            log.debug("Лид уже существует, пропускаем: {}", email);
        }
    }

    private void createDealsForKanban(java.util.List<UUID> leadIds) {
        // Используем валидные адреса вместо "-"
        var addr = new Address("Moscow", "Deal Street", "100000");
        for (int i = 0; i < 3; i++) {
            var lead = addLeadIfNotExistsAndGet("deal" + i + "@example.com", "DealCompany" + i, LeadStatus.QUALIFIED, addr, "+7999123456" + i);
            if (lead != null) {
                var deal = new Deal(lead.id(), new BigDecimal("100000").add(BigDecimal.valueOf(i * 10000)));
                dealRepository.save(deal);
            }
        }
        for (int i = 3; i < 5; i++) {
            var lead = addLeadIfNotExistsAndGet("deal" + i + "@example.com", "DealCompany" + i, LeadStatus.QUALIFIED, addr, "+7999123456" + i);
            if (lead != null) {
                var deal = new Deal(lead.id(), new BigDecimal("150000").add(BigDecimal.valueOf(i * 5000)));
                deal.transitionTo(DealStatus.QUALIFIED);
                dealRepository.save(deal);
            }
        }
        for (int i = 5; i < 7; i++) {
            var lead = addLeadIfNotExistsAndGet("deal" + i + "@example.com", "DealCompany" + i, LeadStatus.QUALIFIED, addr, "+7999123456" + i);
            if (lead != null) {
                var deal = new Deal(lead.id(), new BigDecimal("200000").add(BigDecimal.valueOf(i * 3000)));
                deal.transitionTo(DealStatus.QUALIFIED);
                deal.transitionTo(DealStatus.PROPOSAL_SENT);
                dealRepository.save(deal);
            }
        }
        var lead7 = addLeadIfNotExistsAndGet("deal7@example.com", "DealCompany7", LeadStatus.QUALIFIED, addr, "+79991234567");
        if (lead7 != null) {
            var deal7 = new Deal(lead7.id(), new BigDecimal("250000"));
            deal7.transitionTo(DealStatus.QUALIFIED);
            deal7.transitionTo(DealStatus.PROPOSAL_SENT);
            deal7.transitionTo(DealStatus.NEGOTIATION);
            dealRepository.save(deal7);
        }

        var lead8 = addLeadIfNotExistsAndGet("deal8@example.com", "DealCompany8", LeadStatus.QUALIFIED, addr, "+79991234568");
        if (lead8 != null) {
            var deal8 = new Deal(lead8.id(), new BigDecimal("300000"));
            deal8.transitionTo(DealStatus.QUALIFIED);
            deal8.transitionTo(DealStatus.PROPOSAL_SENT);
            deal8.transitionTo(DealStatus.NEGOTIATION);
            deal8.transitionTo(DealStatus.WON);
            dealRepository.save(deal8);
        }

        var lead9 = addLeadIfNotExistsAndGet("deal9@example.com", "DealCompany9", LeadStatus.QUALIFIED, addr, "+79991234569");
        if (lead9 != null) {
            var deal9 = new Deal(lead9.id(), new BigDecimal("50000"));
            deal9.transitionTo(DealStatus.LOST);
            dealRepository.save(deal9);
        }
    }

    private Lead addLeadIfNotExistsAndGet(String email, String company, LeadStatus status, Address address, String phone) {
        try {
            return leadService.addLead(email, company, status, address, phone);
        } catch (IllegalStateException e) {
            // Лид уже существует - возвращаем существующий лид
            return leadService.findByEmail(email).orElse(null);
        }
    }
}
