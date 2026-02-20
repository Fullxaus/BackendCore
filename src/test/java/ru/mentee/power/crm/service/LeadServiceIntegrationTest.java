package ru.mentee.power.crm.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.spring.repository.DealRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration tests для @Transactional поведения LeadService:
 * rollback при ошибке, self-invocation, LeadProcessor (REQUIRES_NEW).
 */
@SpringBootTest
@ActiveProfiles("test")
public class LeadServiceIntegrationTest {

    @Autowired
    private LeadService leadService;

    @Autowired
    private DealRepository dealRepository;

    @Test
    void convertLeadToDeal_shouldRollbackOnConstraintViolation() {
        // Given: лид в БД
        Address address = new Address("Moscow", "Street", "123456");
        Lead lead = leadService.addLead("rollback-test@example.com", "Company", LeadStatus.QUALIFIED, address, "+79991234567");
        UUID leadId = lead.id();
        assertThat(lead.status()).isEqualTo(LeadStatus.QUALIFIED.name());

        // When: конверсия с amount = null — Deal конструктор бросает NPE (constraint)
        assertThatThrownBy(() -> leadService.convertLeadToDeal(leadId, null))
                .isInstanceOf(NullPointerException.class);

        // Then: Lead остаётся в старом статусе (транзакция откатилась)
        Lead after = leadService.findById(leadId).orElseThrow();
        assertThat(after.status()).isEqualTo(LeadStatus.QUALIFIED.name());

        // Then: в таблице deals нет новой записи по этому leadId
        List<ru.mentee.power.crm.domain.Deal> deals = dealRepository.findAll();
        assertThat(deals).noneMatch(d -> d.getLeadId().equals(leadId));
    }

    @Test
    void convertLeadToDeal_shouldSucceedAndUpdateLeadStatus() {
        Address address = new Address("Moscow", "Street", "123456");
        Lead lead = leadService.addLead("success-convert@example.com", "Company", LeadStatus.QUALIFIED, address, "+79991234568");
        UUID leadId = lead.id();

        ru.mentee.power.crm.domain.Deal deal = leadService.convertLeadToDeal(leadId, new BigDecimal("100000"));

        assertThat(deal).isNotNull();
        assertThat(deal.getLeadId()).isEqualTo(leadId);
        Lead after = leadService.findById(leadId).orElseThrow();
        assertThat(after.status()).isEqualTo(LeadStatus.CONVERTED.name());
        assertThat(dealRepository.findAll()).anyMatch(d -> d.getLeadId().equals(leadId));
    }

    /**
     * Self-invocation: processLeadsSelfInvocation вызывает this.processSingleLead(id).
     * REQUIRES_NEW не срабатывает — все вызовы в одной транзакции родителя.
     * При исключении в одном из processSingleLead откатятся все изменения.
     */
    @Test
    void demonstrateSelfInvocationProblem_allInOneTransaction() {
        Address address = new Address("Moscow", "Street", "123456");
        Lead lead1 = leadService.addLead("self1@example.com", "C1", LeadStatus.NEW, address, "+7001");
        Lead lead2 = leadService.addLead("self2@example.com", "C2", LeadStatus.NEW, address, "+7002");
        List<UUID> ids = List.of(lead1.id(), lead2.id());
        leadService.processLeadsSelfInvocation(ids);
        assertThat(leadService.findById(lead1.id()).orElseThrow().status()).isEqualTo(LeadStatus.PROCESSED.name());
        assertThat(leadService.findById(lead2.id()).orElseThrow().status()).isEqualTo(LeadStatus.PROCESSED.name());
    }

    @Test
    void whenUsingLeadProcessor_eachProcessSingleLeadInSeparateTransaction() {
        Address address = new Address("Moscow", "Street", "123456");
        Lead lead1 = leadService.addLead("proc1@example.com", "C1", LeadStatus.NEW, address, "+7011");
        Lead lead2 = leadService.addLead("proc2@example.com", "C2", LeadStatus.NEW, address, "+7012");
        Lead lead3 = leadService.addLead("proc3@example.com", "C3", LeadStatus.NEW, address, "+7013");

        leadService.processLeads(List.of(lead1.id(), lead2.id(), lead3.id()));

        assertThat(leadService.findById(lead1.id()).orElseThrow().status()).isEqualTo(LeadStatus.PROCESSED.name());
        assertThat(leadService.findById(lead2.id()).orElseThrow().status()).isEqualTo(LeadStatus.PROCESSED.name());
        assertThat(leadService.findById(lead3.id()).orElseThrow().status()).isEqualTo(LeadStatus.PROCESSED.name());
    }


    @Test
    void readOnlyMethod_saveStillExecutes() {
        Address address = new Address("Moscow", "Street", "123456");
        Lead lead = leadService.addLead("readonly@example.com", "Company", LeadStatus.NEW, address, "+7021");
        UUID leadId = lead.id();

        // readOnly = true — подсказка, не запрет: save() внутри метода выполняется
        Lead result = leadService.findByIdReadOnly(leadId);
        assertThat(result).isNotNull();
        assertThat(leadService.findById(leadId)).isPresent();
    }
}
