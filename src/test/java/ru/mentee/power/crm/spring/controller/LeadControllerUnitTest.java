package ru.mentee.power.crm.spring.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.spring.MockLeadService;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
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
}
