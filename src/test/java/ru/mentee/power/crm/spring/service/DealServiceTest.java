package ru.mentee.power.crm.spring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadDomainRepository;
import ru.mentee.power.crm.spring.repository.DealRepository;
import ru.mentee.power.crm.spring.repository.InMemoryDealRepository;
import ru.mentee.power.crm.spring.repository.LeadRepositoryAdapter;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DealServiceTest {

    private DealService dealService;
    private ru.mentee.power.crm.repository.LeadDomainRepository coreLeadRepository;
    private DealRepository dealRepository;

    @BeforeEach
    void setUp() {
        coreLeadRepository = new ru.mentee.power.crm.repository.InMemoryLeadRepository();
        dealRepository = new InMemoryDealRepository();
        var leadRepository = new LeadRepositoryAdapter(coreLeadRepository);
        dealService = new DealService(dealRepository, leadRepository);
    }

    @Test
    void scenario1_convertLeadToDeal_createsDealWithNewStatus() {
        UUID leadId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        Lead lead = new Lead(leadId,
                new Contact("ivan@example.com", "+79991234567", new Address("Moscow", "Lenina", "101000")),
                "Иван Петров",
                LeadStatus.QUALIFIED.name());
        coreLeadRepository.save(lead);

        Deal deal = dealService.convertLeadToDeal(leadId, new BigDecimal("150000.00"));

        assertThat(deal.getStatus()).isEqualTo(DealStatus.NEW);
        assertThat(deal.getAmount()).isEqualByComparingTo("150000.00");
        assertThat(deal.getLeadId()).isEqualTo(leadId);
        assertThat(deal.getCreatedAt()).isNotNull();
    }

    @Test
    void scenario2_validTransition_proposalSentToNegotiation() {
        Deal deal = createDealInStatus(DealStatus.PROPOSAL_SENT);
        dealRepository.save(deal);

        dealService.transitionDealStatus(deal.getId(), DealStatus.NEGOTIATION);

        assertThat(deal.getStatus()).isEqualTo(DealStatus.NEGOTIATION);
    }

    @Test
    void scenario3_invalidTransition_throwsException() {
        Deal deal = createDealInStatus(DealStatus.WON);
        dealRepository.save(deal);

        assertThatThrownBy(() -> dealService.transitionDealStatus(deal.getId(), DealStatus.NEW))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot transition from WON to NEW");
        assertThat(deal.getStatus()).isEqualTo(DealStatus.WON);
    }

    @Test
    void convertLeadToDeal_throwsWhenLeadNotFound() {
        assertThatThrownBy(() -> dealService.convertLeadToDeal(UUID.randomUUID(), BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Lead not found");
    }

    @Test
    void scenario5_qualifiedCanTransitionToProposalSent_notToWon() {
        assertThat(DealStatus.QUALIFIED.canTransitionTo(DealStatus.PROPOSAL_SENT)).isTrue();
        assertThat(DealStatus.QUALIFIED.canTransitionTo(DealStatus.WON)).isFalse();
    }

    private Deal createDealInStatus(DealStatus status) {
        var leadId = UUID.randomUUID();
        var lead = new Lead(leadId, new Contact("x@x.com", "+7", new Address("c", "s", "z")), "Co", "QUALIFIED");
        coreLeadRepository.save(lead);
        var deal = new Deal(leadId, BigDecimal.ONE);
        deal.transitionTo(DealStatus.QUALIFIED);
        if (status != DealStatus.QUALIFIED) {
            deal.transitionTo(DealStatus.PROPOSAL_SENT);
            if (status != DealStatus.PROPOSAL_SENT) {
                deal.transitionTo(DealStatus.NEGOTIATION);
                if (status == DealStatus.WON) {
                    deal.transitionTo(DealStatus.WON);
                } else if (status == DealStatus.LOST) {
                    deal.transitionTo(DealStatus.LOST);
                }
            }
        }
        return deal;
    }
}
