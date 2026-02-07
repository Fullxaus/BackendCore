package ru.mentee.power.crm.spring.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Address;
import ru.mentee.power.crm.domain.Contact;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.spring.MockLeadService;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LeadControllerUnitTest {

    @Mock
    private LeadService mockLeadService;

    @Mock
    private Model model;

    private LeadController controller;

    @BeforeEach
    void setUp() {

        controller = new LeadController(mockLeadService);
    }

    @Test
    void shouldCreateControllerWithoutSpringContainer() {
        assertThat(controller).isNotNull();
    }

    @Test
    void shouldWorkWithMockLeadServiceFromFactory() {

        LeadService mockService = MockLeadService.create();
        LeadController controllerWithMock = new LeadController(mockService);
        assertThat(controllerWithMock).isNotNull();
    }

    @Test
    void shouldDelegateShowLeadsToService_whenNoFilter() {
        when(mockLeadService.findAll()).thenReturn(Collections.emptyList());

        String viewName = controller.showLeads(null, model);

        verify(mockLeadService).findAll();
        verify(model).addAttribute(eq("leads"), any());
        verify(model).addAttribute("currentFilter", null);
        assertThat(viewName).isEqualTo("leads/list");
    }

    @Test
    void shouldDelegateShowLeadsToService_whenStatusFilter() {
        when(mockLeadService.findByStatus(LeadStatus.NEW)).thenReturn(Collections.emptyList());

        String viewName = controller.showLeads(LeadStatus.NEW, model);

        verify(mockLeadService).findByStatus(LeadStatus.NEW);
        verify(model).addAttribute(eq("leads"), any());
        verify(model).addAttribute("currentFilter", LeadStatus.NEW);
        assertThat(viewName).isEqualTo("leads/list");
    }

    @Test
    void shouldReturnCreateFormView() {
        String viewName = controller.showCreateForm(model);

        verify(model).addAttribute("statuses", LeadStatus.values());
        assertThat(viewName).isEqualTo("leads/create");
    }

    @Test
    void showEditForm_shouldReturnFormWithLeadData_whenLeadExists() {
        UUID id = UUID.randomUUID();
        Address address = new Address("City", "Street", "12345");
        Contact contact = new Contact("test@example.com", "+7999", address);
        Lead lead = new Lead(id, contact, "Acme", LeadStatus.NEW.name());
        when(mockLeadService.findById(id)).thenReturn(Optional.of(lead));

        String viewName = controller.showEditForm(id, model);

        verify(mockLeadService).findById(id);
        verify(model).addAttribute("lead", lead);
        verify(model).addAttribute("statuses", LeadStatus.values());
        assertThat(viewName).isEqualTo("edit");
    }

    @Test
    void showEditForm_shouldThrow404_whenLeadNotFound() {
        UUID nonexistentId = UUID.randomUUID();
        when(mockLeadService.findById(nonexistentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.showEditForm(nonexistentId, model))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(rse.getReason()).isEqualTo("Lead not found");
                });
    }

    @Test
    void updateLead_shouldCallServiceAndRedirect() {
        UUID id = UUID.randomUUID();
        when(mockLeadService.update(eq(id), eq("new@example.com"), eq("+7999"), eq("NewCo"), eq(LeadStatus.CONTACTED)))
                .thenReturn(new Lead(id, new Contact("new@example.com", "+7999",
                        new Address("City", "Street", "12345")), "NewCo", LeadStatus.CONTACTED.name()));

        String viewName = controller.updateLead(id, "new@example.com", "+7999", "NewCo", LeadStatus.CONTACTED);

        verify(mockLeadService).update(id, "new@example.com", "+7999", "NewCo", LeadStatus.CONTACTED);
        assertThat(viewName).isEqualTo("redirect:/leads");
    }

    @Test
    void deleteLead_shouldCallServiceAndRedirect() {
        UUID id = UUID.randomUUID();

        String viewName = controller.deleteLead(id);

        verify(mockLeadService).delete(id);
        assertThat(viewName).isEqualTo("redirect:/leads");
    }

    @Test
    void deleteLead_shouldThrow404_whenLeadNotFound() {
        UUID nonexistentId = UUID.randomUUID();
        org.mockito.Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found"))
                .when(mockLeadService).delete(nonexistentId);

        assertThatThrownBy(() -> controller.deleteLead(nonexistentId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                });
    }
}
