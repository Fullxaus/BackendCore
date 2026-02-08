package ru.mentee.power.crm;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.spring.repository.DealRepository;
import ru.mentee.power.crm.spring.service.DealService;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    public static final UUID QUALIFIED_LEAD_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    private final LeadService leadService;
    private final ru.mentee.power.crm.repository.LeadRepository coreLeadRepository;
    private final DealService dealService;
    private final DealRepository dealRepository;

    public DataInitializer(LeadService leadService,
                           ru.mentee.power.crm.repository.LeadRepository coreLeadRepository,
                           DealService dealService,
                           DealRepository dealRepository) {
        this.leadService = leadService;
        this.coreLeadRepository = coreLeadRepository;
        this.dealService = dealService;
        this.dealRepository = dealRepository;
    }

    @Override
    public void run(String... args) {
        leadService.addLead("test1@example.com", "Company1", LeadStatus.NEW, new Address("Moscow", "Suvorova", "123456"), "1234567890");
        leadService.addLead("test2@example.com", "Company2", LeadStatus.NEW, new Address("St.Petersburg", "Pushkinskaya", "987654"), "9876543210");
        leadService.addLead("test3@example.com", "Company3", LeadStatus.NEW, new Address("Kazan", "Kazanskaya", "111111"), "1111111111");
        leadService.addLead("test4@example.com", "Company4", LeadStatus.NEW, new Address("Novosibirsk", "Novosibirskaya", "222222"), "2222222222");
        leadService.addLead("test5@example.com", "Company5", LeadStatus.NEW, new Address("Ekaterinburg", "Lermontova", "333333"), "3333333333");

        // Lead для Scenario 1: convertLeadToDeal
        Lead qualifiedLead = new Lead(QUALIFIED_LEAD_ID,
                new Contact("ivan@example.com", "+79991234567", new Address("Moscow", "Lenina", "101000")),
                "Иван Петров",
                LeadStatus.QUALIFIED.name());
        coreLeadRepository.save(qualifiedLead);

        // 10 Deals для Kanban (Scenario 4): 3 NEW, 2 QUALIFIED, 2 PROPOSAL_SENT, 1 NEGOTIATION, 1 WON, 1 LOST
        var leadIds = leadService.findAll().stream().limit(6).map(Lead::id).toList();
        if (leadIds.size() >= 6) {
            createDealsForKanban(leadIds);
        }
    }

    private void createDealsForKanban(java.util.List<UUID> leadIds) {
        var addr = new Address("-", "-", "-");
        for (int i = 0; i < 3; i++) {
            var lead = leadService.addLead("deal" + i + "@example.com", "DealCompany" + i, LeadStatus.QUALIFIED, addr, "+7");
            var deal = new Deal(lead.id(), new BigDecimal("100000").add(BigDecimal.valueOf(i * 10000)));
            dealRepository.save(deal);
        }
        for (int i = 3; i < 5; i++) {
            var lead = leadService.addLead("deal" + i + "@example.com", "DealCompany" + i, LeadStatus.QUALIFIED, addr, "+7");
            var deal = new Deal(lead.id(), new BigDecimal("150000").add(BigDecimal.valueOf(i * 5000)));
            deal.transitionTo(DealStatus.QUALIFIED);
            dealRepository.save(deal);
        }
        for (int i = 5; i < 7; i++) {
            var lead = leadService.addLead("deal" + i + "@example.com", "DealCompany" + i, LeadStatus.QUALIFIED, addr, "+7");
            var deal = new Deal(lead.id(), new BigDecimal("200000").add(BigDecimal.valueOf(i * 3000)));
            deal.transitionTo(DealStatus.QUALIFIED);
            deal.transitionTo(DealStatus.PROPOSAL_SENT);
            dealRepository.save(deal);
        }
        var lead7 = leadService.addLead("deal7@example.com", "DealCompany7", LeadStatus.QUALIFIED, addr, "+7");
        var deal7 = new Deal(lead7.id(), new BigDecimal("250000"));
        deal7.transitionTo(DealStatus.QUALIFIED);
        deal7.transitionTo(DealStatus.PROPOSAL_SENT);
        deal7.transitionTo(DealStatus.NEGOTIATION);
        dealRepository.save(deal7);

        var lead8 = leadService.addLead("deal8@example.com", "DealCompany8", LeadStatus.QUALIFIED, addr, "+7");
        var deal8 = new Deal(lead8.id(), new BigDecimal("300000"));
        deal8.transitionTo(DealStatus.QUALIFIED);
        deal8.transitionTo(DealStatus.PROPOSAL_SENT);
        deal8.transitionTo(DealStatus.NEGOTIATION);
        deal8.transitionTo(DealStatus.WON);
        dealRepository.save(deal8);

        var lead9 = leadService.addLead("deal9@example.com", "DealCompany9", LeadStatus.QUALIFIED, addr, "+7");
        var deal9 = new Deal(lead9.id(), new BigDecimal("50000"));
        deal9.transitionTo(DealStatus.LOST);
        dealRepository.save(deal9);
    }
}
