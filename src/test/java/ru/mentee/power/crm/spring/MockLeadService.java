package ru.mentee.power.crm.spring;


import ru.mentee.power.crm.service.LeadService;

import static org.mockito.Mockito.mock;


public final class MockLeadService {

    private MockLeadService() {
    }


    public static LeadService create() {
        return mock(LeadService.class);
    }
}
