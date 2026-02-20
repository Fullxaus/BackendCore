package ru.mentee.power.crm.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Демонстрация propagation (REQUIRED, REQUIRES_NEW, MANDATORY) и isolation (READ_COMMITTED, REPEATABLE_READ).
 */
@SpringBootTest
@ActiveProfiles("test")
public class TransactionPropagationDemoTest {

    @Autowired
    private TransactionPropagationDemoService demoService;

    @Autowired
    private LeadService leadService;

    @Test
    void required_participatesInCurrentTransaction() {
        Lead lead = leadService.addLead("req@ex.com", "C", LeadStatus.NEW, new Address("M", "S", "1"), "+7");
        Lead found = demoService.findWithRequired(lead.id());
        assertThat(found).isNotNull();
        assertThat(found.id()).isEqualTo(lead.id());
    }

    @Test
    void requiresNew_createsNewTransaction() {
        Lead lead = leadService.addLead("rnew@ex.com", "C", LeadStatus.NEW, new Address("M", "S", "1"), "+7");
        Lead found = demoService.findWithRequiresNew(lead.id());
        assertThat(found).isNotNull();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void mandatory_throwsWhenCalledWithoutTransaction() {
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> demoService.findWithMandatory(id))
                .isInstanceOf(IllegalTransactionStateException.class);
    }

    @Test
    void mandatory_worksWhenCalledFromRequired() {
        Lead lead = leadService.addLead("mand@ex.com", "C", LeadStatus.NEW, new Address("M", "S", "1"), "+7");
        Lead found = demoService.requiredCallsMandatory(lead.id());
        assertThat(found).isNotNull();
    }

    @Test
    void readCommitted_isolationLevel() {
        Lead lead = leadService.addLead("rc@ex.com", "C", LeadStatus.NEW, new Address("M", "S", "1"), "+7");
        Lead found = demoService.findWithReadCommitted(lead.id());
        assertThat(found).isNotNull();
    }

    @Test
    void repeatableRead_isolationLevel() {
        Lead lead = leadService.addLead("rr@ex.com", "C", LeadStatus.NEW, new Address("M", "S", "1"), "+7");
        Lead found = demoService.findWithRepeatableRead(lead.id());
        assertThat(found).isNotNull();
    }
}
