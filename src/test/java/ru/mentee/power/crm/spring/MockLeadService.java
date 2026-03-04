package ru.mentee.power.crm.spring;

import static org.mockito.Mockito.mock;

import ru.mentee.power.crm.service.LeadService;

public final class MockLeadService {

  private MockLeadService() {}

  public static LeadService create() {
    return mock(LeadService.class);
  }
}
