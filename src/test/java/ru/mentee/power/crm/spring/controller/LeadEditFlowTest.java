package ru.mentee.power.crm.spring.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.service.LeadService;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the full lead edit flow: open form -> update via POST -> redirect -> data in repository.
 */
public class LeadEditFlowTest {

    private LeadController controller;
    private LeadService leadService;
    private LeadRepository repository;
    private Model model;

    @BeforeEach
    void setUp() {
        repository = new LeadRepository();
        leadService = new LeadService(repository);
        controller = new LeadController(leadService);
        model = mock(Model.class);
    }

    @Test
    void fullEditFlow_fromFormToRepositoryUpdate() {
        // Given: existing lead in repository
        Address address = new Address("Moscow", "Tverskaya", "101000");
        Lead existing = new Lead(UUID.randomUUID(),
                new Contact("original@test.com", "+7999", address),
                "OriginalCo", LeadStatus.NEW.name());
        repository.save(existing);
        UUID id = existing.id();

        // When: 1. Open edit form (GET /leads/{id}/edit)
        String formView = controller.showEditForm(id, model);

        // Then: form is displayed with lead data
        assertThat(formView).isEqualTo("edit");
        verify(model).addAttribute(eq("lead"), any(Lead.class));
        verify(model).addAttribute("statuses", LeadStatus.values());

        // When: 2. Submit updated data (POST /leads/{id})
        String redirect = controller.updateLead(
                id, "updated@test.com", "+7111", "UpdatedCo", LeadStatus.CONTACTED);

        // Then: redirect to /leads (Post-Redirect-Get pattern)
        assertThat(redirect).isEqualTo("redirect:/leads");

        // Then: 3. Data is updated in repository
        Lead updated = repository.findById(id);
        assertThat(updated).isNotNull();
        assertThat(updated.contact().email()).isEqualTo("updated@test.com");
        assertThat(updated.contact().phone()).isEqualTo("+7111");
        assertThat(updated.company()).isEqualTo("UpdatedCo");
        assertThat(updated.status()).isEqualTo(LeadStatus.CONTACTED.name());
    }

    @Test
    void editForm_showsPrefilledData() {
        Address address = new Address("City", "Street", "12345");
        Lead lead = new Lead(UUID.randomUUID(),
                new Contact("prefill@test.com", "+7000", address),
                "PrefillCo", LeadStatus.QUALIFIED.name());
        repository.save(lead);

        controller.showEditForm(lead.id(), model);

        verify(model).addAttribute("lead", lead);
    }
}
